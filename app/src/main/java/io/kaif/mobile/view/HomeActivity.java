package io.kaif.mobile.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.kaif.mobile.KaifApplication;
import io.kaif.mobile.R;
import io.kaif.mobile.app.BaseActivity;
import io.kaif.mobile.event.account.SignOutEvent;
import io.kaif.mobile.view.daemon.AccountDaemon;

public class HomeActivity extends BaseActivity {

  @Inject
  AccountDaemon accountDaemon;

  @BindView(R.id.tool_bar)
  Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    ButterKnife.bind(this);
    KaifApplication.getInstance().beans().inject(this);
    setSupportActionBar(toolbar);

    if (!accountDaemon.hasAccount()) {
      showLoginActivityAndFinish();
      return;
    }

    bind(accountDaemon.getSubject(SignOutEvent.class)).subscribe(accountEvent -> {
      Toast.makeText(this, R.string.sign_out_success, Toast.LENGTH_SHORT).show();
      showLoginActivityAndFinish();
    });

    if (savedInstanceState == null) {
      final Fragment fragment = getSupportFragmentManager().findFragmentByTag("hot");
      if (fragment == null) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.container, HomeFragment.newInstance(), "hot")
            .commit();
      }
    }
  }

  private void showLoginActivityAndFinish() {
    startActivity(new Intent(this, LoginActivity.class));
    finish();
  }
}
