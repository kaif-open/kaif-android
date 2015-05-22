package io.kaif.mobile.view.daemon;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import io.kaif.mobile.config.ApiConfiguration;
import io.kaif.mobile.event.EventPublishSubject;
import io.kaif.mobile.event.account.AccountEvent;
import io.kaif.mobile.event.account.SignInSuccessEvent;
import io.kaif.mobile.event.account.SignOutEvent;
import io.kaif.mobile.model.oauth.AccessTokenManager;
import io.kaif.mobile.service.OauthService;
import rx.Observable;

@Singleton
public class AccountDaemon {

  private final EventPublishSubject<AccountEvent> subject;

  private final OauthService oauthService;

  private final AccessTokenManager accessTokenManager;

  private final ApiConfiguration apiConfiguration;

  private String state;

  @Inject
  AccountDaemon(OauthService oauthService,
      AccessTokenManager accessTokenManager,
      ApiConfiguration apiConfiguration) {
    this.oauthService = oauthService;
    this.accessTokenManager = accessTokenManager;
    this.apiConfiguration = apiConfiguration;
    this.subject = new EventPublishSubject<>();
  }

  public Observable<AccountEvent> getSubject(Class<?>... classes) {
    return subject.getSubject(classes);
  }

  public <T extends AccountEvent> Observable<T> getSubject(Class<T> clazz) {
    return subject.getSubject(clazz);
  }

  public Intent createOauthPageIntent() {
    state = UUID.randomUUID().toString();
    final Uri uri = Uri.parse(apiConfiguration.getEndPoint())
        .buildUpon()
        .appendPath("oauth")
        .appendPath("authorize")
        .appendQueryParameter("client_id", apiConfiguration.getClientId())
        .appendQueryParameter("response_type", "code")
        .appendQueryParameter("scope", "public feed article vote user debate")
        .appendQueryParameter("state", state)
        .appendQueryParameter("redirect_uri", apiConfiguration.getRedirectUri())
        .build();
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(uri);
    return intent;
  }

  public boolean hasAccount() {
    return accessTokenManager.hasAccount();
  }

  public void signOut() {
    accessTokenManager.signOut();
    subject.onNext(new SignOutEvent());
  }

  public Observable<Void> accessToken(String code, String state) {
    if (!TextUtils.equals(this.state, state)) {
      return Observable.<Void>error(new IllegalStateException("receive a malicious response"));
    }
    this.state = null;
    return oauthService.getAccessToken(apiConfiguration.getClientId(),
        apiConfiguration.getClientSecret(),
        code,
        apiConfiguration.getRedirectUri(),
        "authorization_code").<Void>map(accessTokenInfo -> {
      accessTokenManager.saveAccount(accessTokenInfo);
      subject.onNext(new SignInSuccessEvent());
      return null;
    });
  }
}
