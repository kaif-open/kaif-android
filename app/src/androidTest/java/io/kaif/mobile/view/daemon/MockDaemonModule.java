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
  AccountDaemon provideAccountDaemon() {
    return Mockito.mock(AccountDaemon.class);
  }

  @Provides
  @Singleton
  ZoneDaemon provideZoneDaemon() {
    return Mockito.mock(ZoneDaemon.class);
  }
}
