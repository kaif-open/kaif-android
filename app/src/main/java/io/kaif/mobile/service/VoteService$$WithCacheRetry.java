package io.kaif.mobile.service;

import java.util.List;

import io.kaif.mobile.model.Vote;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface VoteService$$WithCacheRetry {

  @GET("/v1/vote/article")
  Observable<List<Vote>> listArticleVotes(
      @Query(value = "article-id", encodeValue = false) CommaSeparatedParam articleIds);

  @GET("/v1/vote/debate")
  Observable<List<Vote>> listDebateVotes(
      @Query(value = "debate-id", encodeValue = false) CommaSeparatedParam articleIds);

  @GET("/v1/vote/article")
  @Headers("Cache-Control: public, only-if-cached, max-stale=86400")
  Observable<List<Vote>> listArticleVotes$$WithCacheRetry(
      @Query(value = "article-id", encodeValue = false) CommaSeparatedParam articleIds);

  @GET("/v1/vote/debate")
  @Headers("Cache-Control: public, only-if-cached, max-stale=86400")
  Observable<List<Vote>> listDebateVotes$$WithCacheRetry(
      @Query(value = "debate-id", encodeValue = false) CommaSeparatedParam articleIds);

  @POST("/v1/vote/article")
  Observable<Void> voteArticle(@Body VoteService.VoteArticleEntry voteArticleEntry);

  @POST("/v1/vote/debate")
  Observable<Void> voteDebate(@Body VoteService.VoteDebateEntry voteDebateEntry);
}
