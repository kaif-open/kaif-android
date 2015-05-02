package io.kaif.mobile.service;

import java.util.List;

import io.kaif.mobile.model.Article;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface ArticleService {

  class ExternalLinkEntry {
    String url;
    String title;
    String zone;

    public ExternalLinkEntry(String url, String title, String zone) {
      this.url = url;
      this.title = title;
      this.zone = zone;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      ExternalLinkEntry that = (ExternalLinkEntry) o;

      if (!url.equals(that.url)) {
        return false;
      }
      if (!title.equals(that.title)) {
        return false;
      }
      return zone.equals(that.zone);

    }

    @Override
    public int hashCode() {
      int result = url.hashCode();
      result = 31 * result + title.hashCode();
      result = 31 * result + zone.hashCode();
      return result;
    }
  }

  @PUT("/v1/article/external-link")
  Observable<Article> createExternalLink(@Body ExternalLinkEntry externalLinkEntry);

  @GET("/v1/article/hot")
  Observable<List<Article>> listHotArticles(@Query("start-article-id") String startArticleId);

  @GET("/v1/article/zone/{zone}/external-link/exist")
  Observable<Boolean> exist(@Path("zone") String zone, @Query("url") String url);

  @GET("/v1/article/latest")
  Observable<List<Article>> listLatestArticles(@Query("start-article-id") String startArticleId);

  @GET("/v1/article/{articleId}")
  Observable<Article> loadArticle(@Path("articleId") String articleId);
}
