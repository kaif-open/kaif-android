package io.kaif.mobile.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.kaif.mobile.KaifApplication;
import io.kaif.mobile.R;
import io.kaif.mobile.app.BaseActivity;

public class NewsFeedActivity extends BaseActivity {

  static class NewsFeedActivityIntent extends Intent {

    public NewsFeedActivityIntent(Context context) {
      super(context, NewsFeedActivity.class);
    }

  }

  @Bind(R.id.tool_bar)
  Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_news_feed);
    ButterKnife.bind(this);
    KaifApplication.getInstance().beans().inject(this);
    setSupportActionBar(toolbar);
    setTitle(R.string.news_feed);

    //noinspection ConstantConditions
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
          TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
        } else {
          upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          NavUtils.navigateUpTo(this, upIntent);
        }
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
