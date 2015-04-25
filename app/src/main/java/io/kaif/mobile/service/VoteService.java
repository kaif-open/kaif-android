package io.kaif.mobile.service;

import java.util.List;

import io.kaif.mobile.model.Vote;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface VoteService {

  class VoteArticleEntry {
    String articleId;
    Vote.VoteState voteState;

    public VoteArticleEntry(String articleId, Vote.VoteState voteState) {
      this.articleId = articleId;
      this.voteState = voteState;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      VoteArticleEntry that = (VoteArticleEntry) o;

      if (!articleId.equals(that.articleId)) {
        return false;
      }
      return voteState == that.voteState;

    }

    @Override
    public int hashCode() {
      int result = articleId.hashCode();
      result = 31 * result + voteState.hashCode();
      return result;
    }
  }

  class VoteDebateEntry {
    String debateId;
    Vote.VoteState voteState;

    public VoteDebateEntry(String debateId, Vote.VoteState voteState) {
      this.debateId = debateId;
      this.voteState = voteState;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      VoteDebateEntry that = (VoteDebateEntry) o;

      if (!debateId.equals(that.debateId)) {
        return false;
      }
      return voteState == that.voteState;

    }

    @Override
    public int hashCode() {
      int result = debateId.hashCode();
      result = 31 * result + voteState.hashCode();
      return result;
    }
  }

  @GET("/v1/vote/article")
  Observable<List<Vote>> listArticleVotes(
      @Query(value = "article-id", encodeValue = false) CommaSeparatedParam articleIds);

  @GET("/v1/vote/debate")
  Observable<List<Vote>> listDebateVotes(
      @Query(value = "debate-id", encodeValue = false) CommaSeparatedParam articleIds);

  @POST("/v1/vote/article")
  Observable<Void> voteArticle(@Body VoteArticleEntry voteArticleEntry);

  @POST("/v1/vote/debate")
  Observable<Void> voteDebate(@Body VoteDebateEntry voteDebateEntry);
}
