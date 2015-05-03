package io.kaif.mobile.view;

import javax.inject.Inject;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import io.kaif.mobile.KaifApplication;
import io.kaif.mobile.R;
import io.kaif.mobile.app.BaseActivity;
import io.kaif.mobile.view.daemon.AccountDaemon;
import io.kaif.mobile.view.graphics.drawable.Triangle;
import io.kaif.mobile.view.util.Views;

public class LoginActivity extends BaseActivity {

  @Inject
  AccountDaemon accountDaemon;

  @InjectView(R.id.sign_in)
  Button signInBtn;

  @InjectView(R.id.sign_in_progress)
  ProgressBar signInProgress;

  @InjectView(R.id.sign_in_title)
  TextView signInTitle;

  @InjectView(R.id.title)
  TextView title;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    ButterKnife.inject(this);
    KaifApplication.getInstance().beans().inject(this);

    int triangleSize = (int) -title.getPaint().ascent();
    Triangle triangle = new Triangle(getResources().getColor(R.color.vote_state_up), false);
    triangle.setBounds(new Rect(0, 0, triangleSize, triangleSize));
    title.setCompoundDrawables(triangle, null, null, null);
    title.setCompoundDrawablePadding((int) Views.convertDpToPixel(16, this));

    signInBtn.setOnClickListener(v -> startActivity(accountDaemon.createOauthPageIntent()));
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
  }

  @Override
  protected void onResume() {
    super.onResume();
    final Uri data = getIntent().getData();
    if (data == null) {
      return;
    }
    getIntent().setData(null);

    signInProgress.setVisibility(View.VISIBLE);
    signInTitle.setVisibility(View.VISIBLE);
    signInBtn.setVisibility(View.GONE);
    bind(accountDaemon.accessToken(data.getQueryParameter("code"), data.getQueryParameter("state")))
        .subscribe(aVoid -> {
          Toast.makeText(this, R.string.sign_in_success, Toast.LENGTH_SHORT).show();
          Intent intent = new Intent(this, HomeActivity.class);
          startActivity(intent);
          finish();
        }, throwable -> {
          Toast.makeText(this, throwable.toString(), Toast.LENGTH_SHORT).show();
          signInProgress.setVisibility(View.GONE);
          signInTitle.setVisibility(View.GONE);
          signInBtn.setVisibility(View.VISIBLE);
        });
  }
}
