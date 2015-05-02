package io.kaif.mobile.view;

import java.io.Serializable;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import io.kaif.mobile.KaifApplication;
import io.kaif.mobile.R;
import io.kaif.mobile.app.BaseActivity;
import io.kaif.mobile.model.Article;
import io.kaif.mobile.view.daemon.ArticleDaemon;
import io.kaif.mobile.view.viewmodel.ArticleViewModel;
import io.kaif.mobile.view.viewmodel.DebateViewModel;

public class DebatesActivity extends BaseActivity {

  static class DebatesActivityIntent {

    private static final String ARTICLE = "ARTICLE";
    private static final String ARTICLE_ID = "ARTICLE_ID";
    private static final String DEBATE_ID = "DEBATE_ID";

    public static Intent create(Context context, ArticleViewModel article) {
      final Intent intent = new Intent(context, DebatesActivity.class);
      intent.putExtra(ARTICLE, article);
      return intent;
    }

    public static ArticleViewModel getArticle(Bundle bundle) {
      Serializable serializable = bundle.getSerializable(ARTICLE);
      return (ArticleViewModel) serializable;
    }

    public static Intent create(Context context, DebateViewModel debateViewModel) {
      final Intent intent = new Intent(context, DebatesActivity.class);
      intent.putExtra(ARTICLE_ID, debateViewModel.getArticleId());
      intent.putExtra(DEBATE_ID, debateViewModel.getDebateId());
      return intent;
    }

    public static String getArticleId(Bundle bundle) {
      return bundle.getString(ARTICLE_ID);
    }

    public static String getDebateId(Bundle bundle) {
      return bundle.getString(DEBATE_ID);
    }

  }

  public static final String ARTICLE_KEY = "ARTICLE";

  @InjectView(R.id.tool_bar)
  Toolbar toolbar;

  private ArticleViewModel article;

  @Inject
  ArticleDaemon articleDaemon;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_debates);
    ButterKnife.inject(this);
    KaifApplication.getInstance().beans().inject(this);
    setSupportActionBar(toolbar);

    //noinspection ConstantConditions
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    if (savedInstanceState != null) {
      article = (ArticleViewModel) savedInstanceState.getSerializable(ARTICLE_KEY);
      if (article != null) {
        createDebatesFragment(false);
        return;
      }
    }

    article = DebatesActivityIntent.getArticle(getIntent().getExtras());
    if (article != null) {
      createDebatesFragment(true);
      return;
    }

    final String articleId = DebatesActivityIntent.getArticleId(getIntent().getExtras());
    bind(articleDaemon.loadArticle(articleId)).subscribe(articleViewModel -> {
      article = articleViewModel;
      createDebatesFragment(true);
    }, throwable -> {
      Toast.makeText(this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
      finish();
    });
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putSerializable("ARTICLE", article);
    super.onSaveInstanceState(outState);
  }

  private void createDebatesFragment(boolean recreateFragment) {
    if (article.getArticleType() == Article.ArticleType.EXTERNAL_LINK) {
      setTitle(R.string.external_link);
    } else {
      setTitle(R.string.speak);
    }
    if (recreateFragment) {
      final String debateId = DebatesActivityIntent.getDebateId(getIntent().getExtras());
      DebatesFragment debatesFragment = DebatesFragment.newInstance(article, debateId);
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.content, debatesFragment, DebatesFragment.class.getSimpleName())
          .commit();
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
