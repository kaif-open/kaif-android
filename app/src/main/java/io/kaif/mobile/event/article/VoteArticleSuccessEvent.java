package io.kaif.mobile.event.article;

import io.kaif.mobile.model.Vote;

public class VoteArticleSuccessEvent extends ArticleEvent {
  private final String articleId;
  private final Vote.VoteState voteState;

  public VoteArticleSuccessEvent(String articleId, Vote.VoteState voteState) {
    this.articleId = articleId;
    this.voteState = voteState;
  }

  public String getArticleId() {
    return articleId;
  }

  public Vote.VoteState getVoteState() {
    return voteState;
  }

  @Override
  public String toString() {
    return "VoteArticleSuccessEvent{" +
        "articleId='" + articleId + '\'' +
        ", voteState=" + voteState +
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

    VoteArticleSuccessEvent that = (VoteArticleSuccessEvent) o;

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
