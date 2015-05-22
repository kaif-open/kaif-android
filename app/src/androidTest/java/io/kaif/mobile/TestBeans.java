package io.kaif.mobile;

import javax.inject.Singleton;

import dagger.Component;
import io.kaif.mobile.view.HomeActivityTest;
import io.kaif.mobile.view.LoginActivityTest;
import io.kaif.mobile.view.daemon.MockDaemonModule;

@Singleton
@Component(modules = MockDaemonModule.class)
public interface TestBeans extends Beans {

  void inject(HomeActivityTest homeActivityTest);

  void inject(LoginActivityTest loginActivityTest);
}
