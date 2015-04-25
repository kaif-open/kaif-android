package io.kaif.mobile.view.share;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import io.kaif.mobile.KaifApplication;
import io.kaif.mobile.R;
import io.kaif.mobile.app.BaseFragment;
import io.kaif.mobile.model.Zone;
import io.kaif.mobile.view.daemon.ArticleDaemon;
import io.kaif.mobile.view.daemon.ZoneDaemon;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;

public class ShareExternalLinkFragment extends BaseFragment {

  @InjectView(R.id.title)
  EditText titleEditText;

  @InjectView(R.id.url)
  EditText urlEditText;

  @InjectView(R.id.zone_name)
  Spinner zoneNameSpinner;

  @InjectView(R.id.share_btn)
  Button shareBtn;

  @Inject
  ArticleDaemon articleDaemon;

  @Inject
  ZoneDaemon zoneDaemon;

  public static ShareExternalLinkFragment newInstance() {
    ShareExternalLinkFragment fragment = new ShareExternalLinkFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  public ShareExternalLinkFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {

    final View view = inflater.inflate(R.layout.fragment_share_external_link, container, false);
    ButterKnife.inject(this, view);

    KaifApplication.getInstance().beans().inject(this);

    setupView();

    fillContent();

    return view;
  }

  private void setupView() {
    bind(WidgetObservable.text(titleEditText)).map(OnTextChangeEvent::text)
        .map(t -> t.length() >= 3)
        .distinctUntilChanged()
        .subscribe(shareBtn::setEnabled);

    shareBtn.setOnClickListener(v -> {
      shareBtn.setEnabled(false);
      bind(articleDaemon.createExternalLink(urlEditText.getText().toString(),
          titleEditText.getText().toString(),
          (String) zoneNameSpinner.getSelectedItem())).subscribe(aVoid -> {
        shareBtn.setEnabled(true);
        getActivity().finish();
      }, throwable -> {
        Toast.makeText(getActivity(), throwable.toString(), Toast.LENGTH_SHORT).show();
        shareBtn.setEnabled(true);
      });
    });
  }

  private void fillContent() {
    Bundle bundle = getActivity().getIntent().getExtras();
    String rawUrl = bundle.getString(Intent.EXTRA_TEXT);
    Matcher matcher = Pattern.compile(".*(http[^\\s]+).*").matcher(rawUrl);
    if (matcher.matches()) {
      String cleanUrl = matcher.group(1);
      String subject = bundle.getString(Intent.EXTRA_SUBJECT);
      titleEditText.setText(subject);
      urlEditText.setText(cleanUrl);
    }

    bind(zoneDaemon.listAll()).subscribe(zones -> {
      final ArrayAdapter<String> zoneAdapter = new ArrayAdapter<>(getActivity(),
          android.R.layout.simple_spinner_item);
      for (Zone zone : zones) {
        zoneAdapter.add(zone.getName());
      }
      zoneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      zoneNameSpinner.setAdapter(zoneAdapter);
    }, throwable -> {
      throwable.printStackTrace();
      Toast.makeText(getActivity(), throwable.toString(), Toast.LENGTH_SHORT).show();
    });

  }
}
