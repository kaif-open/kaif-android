package io.kaif.mobile.view;

import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
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
import io.kaif.mobile.R;
import io.kaif.mobile.TestBeans;
import io.kaif.mobile.view.daemon.AccountDaemon;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.intent.matcher.UriMatchers.hasHost;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

  @Inject
  AccountDaemon accountDaemon;

  @Rule
  public ActivityTestRule<LoginActivity> activityRule = new ActivityTestRule<>(LoginActivity.class,
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
  public void showOauthPage() {

    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://foo.com"));

    Mockito.when(accountDaemon.createOauthPageIntent()).thenReturn(intent);

    activityRule.launchActivity(new Intent());

    onView(withId(R.id.sign_in)).perform(click());

    intended(allOf(hasAction(equalTo(Intent.ACTION_VIEW)),
        hasData(hasHost(equalTo("foo.com")))));
  }

}