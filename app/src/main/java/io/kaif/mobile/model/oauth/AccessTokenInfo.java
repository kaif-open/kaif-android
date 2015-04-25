package io.kaif.mobile.model.oauth;

import com.google.gson.annotations.SerializedName;

public class AccessTokenInfo {

  @SerializedName("token_type")
  private final String tokenType;

  @SerializedName("access_token")
  private final String accessToken;

  private final String scope;

  public AccessTokenInfo(String tokenType, String accessToken, String scope) {
    this.tokenType = tokenType;
    this.accessToken = accessToken;
    this.scope = scope;
  }

  public String getAuthorization() {
    return tokenType + " " + accessToken;
  }

  @Override
  public String toString() {
    return "AccessTokenInfo{" +
        "tokenType='" + tokenType + '\'' +
        ", accessToken='" + accessToken + '\'' +
        ", scope='" + scope + '\'' +
        '}';
  }
}