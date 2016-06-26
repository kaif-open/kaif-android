package io.kaif.mobile.service;

import java.util.List;

import io.kaif.mobile.model.Debate;
import io.kaif.mobile.model.DebateNode;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface DebateService {

  class CreateDebateEntry {
    String articleId;
    String parentDebateId;
    String content;

    public CreateDebateEntry(String articleId, String parentDebateId, String content) {
      this.articleId = articleId;
      this.parentDebateId = parentDebateId;
      this.content = content;
    }

    @Override
    public String toString() {
      return "CreateDebateEntry{" +
          "articleId='" + articleId + '\'' +
          ", parentDebateId='" + parentDebateId + '\'' +
          ", content='" + content + '\'' +
          '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      CreateDebateEntry that = (CreateDebateEntry) o;

      if (!articleId.equals(that.articleId)) {
        return false;
      }
      if (parentDebateId != null
          ? !parentDebateId.equals(that.parentDebateId)
          : that.parentDebateId != null) {
        return false;
      }
      return content.equals(that.content);

    }

    @Override
    public int hashCode() {
      int result = articleId.hashCode();
      result = 31 * result + (parentDebateId != null ? parentDebateId.hashCode() : 0);
      result = 31 * result + content.hashCode();
      return result;
    }
  }

  @GET("/v1/debate/latest")
  Observable<List<Debate>> listLatestDebates(@Query("start-debate-id") String startDebateId);


  @GET("/v1/debate/article/{articleId}/tree")
  Observable<DebateNode> getDebateTree(@Path("articleId") String articleId);

  @PUT("/v1/debate")
  Observable<Debate> debate(@Body CreateDebateEntry createDebateEntry);
}
