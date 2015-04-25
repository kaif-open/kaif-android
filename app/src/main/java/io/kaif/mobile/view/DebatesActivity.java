package io.kaif.mobile.view;

import java.io.Serializable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.ButterKnife;
import butterknife.InjectView;
import io.kaif.mobile.KaifApplication;
import io.kaif.mobile.R;
import io.kaif.mobile.app.BaseActivity;
import io.kaif.mobile.model.Article;
import io.kaif.mobile.view.viewmodel.ArticleViewModel;

public class DebatesActivity extends BaseActivity {

  static class DebatesActivityIntent {

    private static final String ARTICLE = "ARTICLE";

    public static Intent create(Context context, ArticleViewModel article) {
      final Intent intent = new Intent(context, DebatesActivity.class);
      intent.putExtra(ARTICLE, article);
      return intent;
    }

    public static ArticleViewModel getArticle(Bundle bundle) {
      Serializable serializable = bundle.getSerializable(ARTICLE);
      return (ArticleViewModel) serializable;
    }

  }

  @InjectView(R.id.tool_bar)
  Toolbar toolbar;

  private ArticleViewModel article;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_debates);
    ButterKnife.inject(this);
    KaifApplication.getInstance().beans().inject(this);
    setSupportActionBar(toolbar);

    article = DebatesActivityIntent.getArticle(getIntent().getExtras());
    if (article.getArticleType() == Article.ArticleType.EXTERNAL_LINK) {
      setTitle(R.string.external_link);
    } else {
      setTitle(R.string.speak);
    }
    //noinspection ConstantConditions
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    if (savedInstanceState == null) {
      DebatesFragment debatesFragment = new DebatesFragment();
      debatesFragment.setArguments(getIntent().getExtras());
      getSupportFragmentManager().beginTransaction().add(R.id.content, debatesFragment).commit();
    }

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_debates, menu);
    return true;
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
      case R.id.action_open_link:
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(article.getPermaLink());
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        this.startActivity(intent);
        return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
