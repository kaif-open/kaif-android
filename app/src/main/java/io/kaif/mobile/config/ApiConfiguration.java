package io.kaif.mobile.config;

public class ApiConfiguration {

  private String endPoint;

  private String clientId;

  private String redirectUri;

  private String clientSecret;

  public ApiConfiguration(String endPoint,
      String clientId,
      String clientSecret,
      String redirectUri) {
    this.endPoint = endPoint;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.redirectUri = redirectUri;
  }

  public String getEndPoint() {
    return endPoint;
  }

  public String getClientId() {
    return clientId;
  }

  public String getRedirectUri() {
    return redirectUri;
  }

  public String getClientSecret() {
    return clientSecret;
  }
}
