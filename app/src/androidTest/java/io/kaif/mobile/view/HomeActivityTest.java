package io.kaif.mobile.view;

import static org.junit.Assert.*;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;
import io.kaif.mobile.DaggerTestBeans;
import io.kaif.mobile.KaifApplication;
import io.kaif.mobile.TestBeans;
import io.kaif.mobile.view.daemon.AccountDaemon;
import io.kaif.mobile.view.daemon.MockDaemonModule;

@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {

  @Inject
  AccountDaemon accountDaemon;

  @Rule
  public ActivityTestRule<HomeActivity> activityRule = new ActivityTestRule<>(HomeActivity.class,
      true,
      false);

  @Before
  public void setUp() {
    Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
    KaifApplication app = (KaifApplication) instrumentation.getTargetContext()
        .getApplicationContext();
    TestBeans beans = DaggerTestBeans.builder().mockDaemonModule(new MockDaemonModule()).build();
    app.setBeans(beans);
    beans.inject(this);
  }

  @MediumTest
  @Test
  public void showLoginActivity_if_not_signIn() {
    Mockito.when(accountDaemon.hasAccount()).thenReturn(false);
    Instrumentation.ActivityMonitor activityMonitor = InstrumentationRegistry.getInstrumentation()
        .addMonitor(LoginActivity.class.getName(), null, false);

    activityRule.launchActivity(new Intent());

    Activity loginActivity = InstrumentationRegistry.getInstrumentation()
        .waitForMonitorWithTimeout(activityMonitor, 3);
    assertNotNull(loginActivity);
    loginActivity.finish();
  }

}