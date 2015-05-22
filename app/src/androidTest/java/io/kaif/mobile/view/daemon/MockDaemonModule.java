package io.kaif.mobile.view.daemon;

import javax.inject.Singleton;

import org.mockito.Mockito;

import dagger.Module;
import dagger.Provides;

@Module
public class MockDaemonModule {

  @Provides
  @Singleton
  ArticleDaemon provideArticleDaemon() {
    return Mockito.mock(ArticleDaemon.class);
  }

  @Provides
  @Singleton
  DebateDaemon provideDebateDaemon() {
    return Mockito.mock(DebateDaemon.class);
  }

  @Provides
  @Singleton
  AccountDaemon provideAccountDaemon() {
    return Mockito.mock(AccountDaemon.class);
  }

  @Provides
  @Singleton
  ZoneDaemon provideZoneDaemon() {
    return Mockito.mock(ZoneDaemon.class);
  }

  @Provides
  @Singleton
  VoteDaemon provideVoteDaemon() {
    return Mockito.mock(VoteDaemon.class);
  }

  @Provides
  @Singleton
  FeedDaemon provideNewsFeedDaemon() {
    return Mockito.mock(FeedDaemon.class);
  }
}
