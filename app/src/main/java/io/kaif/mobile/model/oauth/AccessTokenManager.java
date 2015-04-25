package io.kaif.mobile.model.oauth;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gson.Gson;

import android.content.SharedPreferences;

@Singleton
public class AccessTokenManager {

  public static final String ACCESS_TOKEN_KEY = "ACCESS_TOKEN";

  SharedPreferences preference;

  Gson gson;

  @Inject
  public AccessTokenManager(SharedPreferences preference, Gson gson) {
    this.gson = gson;
    this.preference = preference;
  }

  public boolean hasAccount() {
    return preference.contains(ACCESS_TOKEN_KEY);
  }

  public void signOut() {
    preference.edit().remove(ACCESS_TOKEN_KEY).apply();
  }

  public void saveAccount(AccessTokenInfo accessTokenInfo) {
    preference.edit().putString(ACCESS_TOKEN_KEY, gson.toJson(accessTokenInfo)).apply();
  }

  public AccessTokenInfo findAccount() {
    return gson.fromJson(preference.getString(ACCESS_TOKEN_KEY, null), AccessTokenInfo.class);
  }
}
