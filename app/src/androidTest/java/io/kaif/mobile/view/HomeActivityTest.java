package io.kaif.mobile.view;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.inject.Inject;

import io.kaif.mobile.DaggerTestBeans;
import io.kaif.mobile.KaifApplication;
import io.kaif.mobile.TestBeans;
import io.kaif.mobile.view.daemon.AccountDaemon;

import static android.support.test.espresso.intent.Intents.intended;

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
    TestBeans beans = DaggerTestBeans.builder().build();
    app.setBeans(beans);
    beans.inject(this);
    Intents.init();
  }

  @After
  public void tearDown() {
    Intents.release();
  }

  @Test
  public void showLoginActivity_if_not_signIn() {
    Mockito.when(accountDaemon.hasAccount()).thenReturn(false);
    activityRule.launchActivity(new Intent());
    intended(IntentMatchers.hasComponent(LoginActivity.class.getName()));
  }

}