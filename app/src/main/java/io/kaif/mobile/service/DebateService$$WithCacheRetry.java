package io.kaif.mobile.service;

import java.util.List;

import io.kaif.mobile.model.Debate;
import io.kaif.mobile.model.DebateNode;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface DebateService$$WithCacheRetry {

  @GET("/v1/debate/latest")
  Observable<List<Debate>> listLatestDebates(@Query("start-debate-id") String startDebateId);

  @GET("/v1/debate/article/{articleId}/tree")
  Observable<DebateNode> getDebateTree(@Path("articleId") String articleId);

  @GET("/v1/debate/latest")
  @Headers("Cache-Control: public, only-if-cached, max-stale=86400")
  Observable<List<Debate>> listLatestDebates$$WithCacheRetry(
      @Query("start-debate-id") String startDebateId);

  @GET("/v1/debate/article/{articleId}/tree")
  @Headers("Cache-Control: public, only-if-cached, max-stale=86400")
  Observable<DebateNode> getDebateTree$$WithCacheRetry(@Path("articleId") String articleId);

  @PUT("/v1/debate")
  Observable<Debate> debate(@Body DebateService.CreateDebateEntry createDebateEntry);
}
