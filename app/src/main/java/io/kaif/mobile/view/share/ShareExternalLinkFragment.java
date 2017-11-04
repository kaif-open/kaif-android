package io.kaif.mobile.view.share;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.kaif.mobile.KaifApplication;
import io.kaif.mobile.R;
import io.kaif.mobile.app.BaseFragment;
import io.kaif.mobile.model.Article;
import io.kaif.mobile.model.Zone;
import io.kaif.mobile.model.exception.DuplicateArticleUrlException;
import io.kaif.mobile.view.daemon.ArticleDaemon;
import io.kaif.mobile.view.daemon.ZoneDaemon;
import rx.Observable;

public class ShareExternalLinkFragment extends BaseFragment {

  public static ShareExternalLinkFragment newInstance() {
    ShareExternalLinkFragment fragment = new ShareExternalLinkFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  MenuItem shareBtn;

  @BindView(R.id.title)
  EditText titleEditText;

  @BindView(R.id.url)
  EditText urlEditText;

  @BindView(R.id.zone_name)
  Spinner zoneNameSpinner;

  @Inject
  ArticleDaemon articleDaemon;

  @Inject
  ZoneDaemon zoneDaemon;

  private Pattern urlPattern = Pattern.compile(".*(http[^\\s]+).*");

  public ShareExternalLinkFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_share, menu);
    shareBtn = menu.findItem(R.id.action_share);
    shareBtn.setEnabled(false);

    RxTextView.textChanges(titleEditText)
        .map(t -> t.length() >= 3)
        .distinctUntilChanged()
        .subscribe(shareBtn::setEnabled);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_share) {
      item.setEnabled(false);
      createArticle();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void createArticle() {
    shareBtn.setEnabled(false);
    doCreateArticle(false).onErrorResumeNext(throwable -> {
      if (throwable instanceof DuplicateArticleUrlException) {
        return createDuplicateDialog().flatMap(confirm -> {
          if (confirm) {
            return doCreateArticle(true);
          } else {
            return Observable.<Article>error(throwable);
          }
        });
      }
      return Observable.error(throwable);
    }).subscribe(article -> {
      getActivity().finish();
    }, throwable -> {
      shareBtn.setEnabled(true);
      Toast.makeText(getActivity(), throwable.toString(), Toast.LENGTH_SHORT).show();
    });
  }

  private Observable<Article> doCreateArticle(boolean forceCreate) {
    return bind(articleDaemon.createExternalLink(urlEditText.getText().toString(),
        titleEditText.getText().toString(),
        (String) zoneNameSpinner.getSelectedItem(),
        forceCreate));
  }

  @NonNull
  private Observable<Boolean> createDuplicateDialog() {
    return Observable.create(subscriber -> {
      new AlertDialog.Builder(getActivity()).setTitle(R.string.warning)
          .setMessage(R.string.url_exist)
          .setPositiveButton(R.string.force_create, (dialog, whichButton) -> {
            subscriber.onNext(true);
            subscriber.onCompleted();
          })
          .setNegativeButton(R.string.dialog_cancel, (dialog, whichButton) -> {
            subscriber.onNext(false);
            subscriber.onCompleted();
          })
          .setOnCancelListener(dialogInterface -> {
            subscriber.onNext(false);
            subscriber.onCompleted();
          })
          .create()
          .show();
    });
  }


  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {

    setHasOptionsMenu(true);
    final View view = inflater.inflate(R.layout.fragment_share_external_link, container, false);
    ButterKnife.bind(this, view);

    KaifApplication.getInstance().beans().inject(this);

    setupView();

    fillContent();

    return view;
  }

  private void setupView() {

  }

  private void fillContent() {
    Bundle bundle = getActivity().getIntent().getExtras();
    String rawUrl = bundle.getString(Intent.EXTRA_TEXT);
    Matcher matcher = urlPattern.matcher(rawUrl);
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
