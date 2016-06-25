package io.kaif.mobile.service;

import java.util.List;

import io.kaif.mobile.model.Zone;
import retrofit2.http.GET;
import rx.Observable;

public interface ZoneService {

  @GET("/v1/zone/all")
  Observable<List<Zone>> listAll();
}
