package io.kaif.mobile.view.widget;

import javax.inject.Inject;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import io.kaif.mobile.KaifApplication;
import io.kaif.mobile.R;
import io.kaif.mobile.view.daemon.ArticleDaemon;
import rx.android.app.support.RxDialogFragment;

public class ReplyDialog extends RxDialogFragment implements TextView.OnEditorActionListener {

  public static final int MIN_CONTENT_SIZE = 5;

  public static ReplyDialog createFragment(String article, String parentDebateId, int level) {
    ReplyDialog replyDialog = new ReplyDialog();
    Bundle args = new Bundle();
    args.putString("PARENT_DEBATE_ID", parentDebateId);
    args.putString("ARTICLE_ID", article);
    args.putInt("LEVEL", level);
    replyDialog.setArguments(args);
    return replyDialog;
  }

  private String getParentDebateId() {
    return getArguments().getString("PARENT_DEBATE_ID");
  }

  private String getArticleId() {
    return getArguments().getString("ARTICLE_ID");
  }

  private int getLevel() {
    return getArguments().getInt("LEVEL");
  }

  @Inject
  ArticleDaemon articleDaemon;

  @InjectView(R.id.debate_content)
  protected EditText contentEditText;

  public ReplyDialog() {
    // Empty constructor required for DialogFragment
  }

  @Override
  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    if (EditorInfo.IME_ACTION_DONE == actionId) {
      submitDebate();
      this.dismiss();
      return true;
    }
    return false;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    KaifApplication.getInstance().beans().inject(this);
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View view = inflater.inflate(R.layout.fragment_reply, null);
    ButterKnife.inject(this, view);
    contentEditText.setOnEditorActionListener(this);
    return new AlertDialog.Builder(getActivity()).setTitle(R.string.reply)
        .setView(view)
        .setPositiveButton(R.string.submit_reply, (dialog, whichButton) -> {
          submitDebate();
        })
        .setNegativeButton(R.string.dialog_cancel, (dialog, whichButton) -> {
        })
        .create();
  }

  private void submitDebate() {
    String debateContent = contentEditText.getText().toString().trim();
    if (debateContent.length() < MIN_CONTENT_SIZE) {
      Toast.makeText(getActivity(), R.string.debate_too_shor, Toast.LENGTH_SHORT).show();
      return;
    }
    articleDaemon.debate(getArticleId(), getParentDebateId(), getLevel(), debateContent);
  }
}
