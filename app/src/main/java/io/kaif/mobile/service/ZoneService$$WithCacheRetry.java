package io.kaif.mobile.service;

import java.util.List;

import io.kaif.mobile.model.Zone;
import retrofit.http.GET;
import retrofit.http.Headers;
import rx.Observable;

public interface ZoneService$$WithCacheRetry {

  @GET("/v1/zone/all")
  Observable<List<Zone>> listAll();

  @GET("/v1/zone/all")
  @Headers("Cache-Control: public, only-if-cached, max-stale=86400")
  Observable<List<Zone>> listAll$$WithCacheRetry();
}
