package io.kaif.mobile.view.share;

import javax.inject.Inject;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import io.kaif.mobile.KaifApplication;
import io.kaif.mobile.R;
import io.kaif.mobile.app.BaseActivity;
import io.kaif.mobile.view.daemon.AccountDaemon;

public class ShareArticleActivity extends BaseActivity {

  @InjectView(R.id.tool_bar)
  Toolbar toolbar;

  @Inject
  AccountDaemon accountDaemon;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_share_article);
    ButterKnife.inject(this);

    KaifApplication.getInstance().beans().inject(this);
    setSupportActionBar(toolbar);

    if (!accountDaemon.hasAccount()) {
      Toast.makeText(this, R.string.not_sign_in_warning, Toast.LENGTH_SHORT).show();
      finish();
    }

  }
}
