package io.kaif.mobile.service;

import io.kaif.mobile.model.oauth.AccessTokenInfo;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;

public interface OauthService {

  @FormUrlEncoded
  @POST("/oauth/access-token")
  Observable<AccessTokenInfo> getAccessToken(@Field("client_id") String clientId,
      @Field("client_secret") String clientSecret,
      @Field("code") String code,
      @Field("redirect_uri") String redirectUri,
      @Field("grant_type") String grantType);

}
