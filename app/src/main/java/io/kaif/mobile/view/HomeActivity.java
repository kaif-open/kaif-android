package io.kaif.mobile.view;

import javax.inject.Inject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import io.kaif.mobile.KaifApplication;
import io.kaif.mobile.R;
import io.kaif.mobile.app.BaseActivity;
import io.kaif.mobile.event.account.SignOutEvent;
import io.kaif.mobile.view.daemon.AccountDaemon;

public class HomeActivity extends BaseActivity {

  @Inject
  AccountDaemon accountDaemon;

  @InjectView(R.id.tool_bar)
  Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    ButterKnife.inject(this);
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
            .replace(R.id.container, HotArticlesFragment.newInstance(), "hot")
            .commit();
      }
    }
  }

  private void showLoginActivityAndFinish() {
    startActivity(new Intent(this, LoginActivity.class));
    finish();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_home, menu);
    if (!accountDaemon.hasAccount()) {
      menu.findItem(R.id.action_sign_out).setVisible(false);
    }
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_sign_out) {
      accountDaemon.signOut();
      return true;
    } else if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
