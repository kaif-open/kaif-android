package io.kaif.mobile.service;

import java.util.List;

import io.kaif.mobile.model.Article;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface ArticleService$$WithCacheRetry {

  @PUT("/v1/article/external-link")
  Observable<Article> createExternalLink(@Body ArticleService.ExternalLinkEntry externalLinkEntry);

  @GET("/v1/article/hot")
  Observable<List<Article>> listHotArticles(@Query("start-article-id") String startArticleId);

  @GET("/v1/article/hot")
  @Headers("Cache-Control: public, only-if-cached, max-stale=86400")
  Observable<List<Article>> listHotArticles$$WithCacheRetry(
      @Query("start-article-id") String startArticleId);

  @GET("/v1/article/zone/{zone}/external-link/exist")
  Observable<Boolean> exist(@Path("zone") String zone, @Query("url") String url);

  @GET("/v1/article/zone/{zone}/external-link/exist")
  @Headers("Cache-Control: public, only-if-cached, max-stale=86400")
  Observable<Boolean> exist$$WithCacheRetry(@Path("zone") String zone, @Query("url") String url);

  @GET("/v1/article/latest")
  Observable<List<Article>> listLatestArticles(@Query("start-article-id") String startArticleId);

  @GET("/v1/article/latest")
  @Headers("Cache-Control: public, only-if-cached, max-stale=86400")
  Observable<List<Article>> listLatestArticles$$WithCacheRetry(
      @Query("start-article-id") String startArticleId);

  @GET("/v1/article/{articleId}")
  Observable<Article> loadArticle(@Path("articleId") String articleId);

  @GET("/v1/article/{articleId}")
  @Headers("Cache-Control: public, only-if-cached, max-stale=86400")
  Observable<Article> loadArticle$$WithCacheRetry(@Path("articleId") String articleId);
}
