package io.kaif.mobile;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import io.kaif.mobile.service.ServiceModule;
import io.kaif.mobile.util.UtilModule;
import io.kaif.mobile.view.ArticlesFragment;
import io.kaif.mobile.view.DebatesActivity;
import io.kaif.mobile.view.DebatesFragment;
import io.kaif.mobile.view.HomeActivity;
import io.kaif.mobile.view.HomeFragment;
import io.kaif.mobile.view.HonorFragment;
import io.kaif.mobile.view.LatestDebatesFragment;
import io.kaif.mobile.view.LoginActivity;
import io.kaif.mobile.view.NewsFeedActivity;
import io.kaif.mobile.view.NewsFeedActivityFragment;
import io.kaif.mobile.view.share.ShareArticleActivity;
import io.kaif.mobile.view.share.ShareExternalLinkFragment;
import io.kaif.mobile.view.widget.ArticleScoreTextView;
import io.kaif.mobile.view.widget.DebateActions;
import io.kaif.mobile.view.widget.ReplyDialog;
import io.kaif.mobile.view.widget.VoteArticleButton;

@Singleton
@Component(modules = { ServiceModule.class, UtilModule.class })
public interface Beans {

  void inject(HomeActivity activity);

  void inject(KaifApplication kaifApplication);

  void inject(ShareArticleActivity activity);

  void inject(ShareExternalLinkFragment fragment);

  void inject(ArticlesFragment fragment);

  void inject(DebatesActivity debatesActivity);

  void inject(DebatesFragment debatesFragment);

  void inject(VoteArticleButton view);

  void inject(ArticleScoreTextView view);

  void inject(DebateActions debateActions);

  void inject(ReplyDialog replyDialog);

  void inject(HomeFragment homeFragment);

  void inject(LoginActivity loginActivity);

  void inject(LatestDebatesFragment latestDebatesFragment);

  void inject(NewsFeedActivity newsFeedActivity);

  void inject(NewsFeedActivityFragment newsFeedActivityFragment);

  void inject(HonorFragment honorFragment);

  final class Initializer {
    public static Beans init(Application application) {
      return DaggerBeans.builder()
          .utilModule(new UtilModule(application))
          .serviceModule(new ServiceModule(application))
          .build();
    }
  }
}
