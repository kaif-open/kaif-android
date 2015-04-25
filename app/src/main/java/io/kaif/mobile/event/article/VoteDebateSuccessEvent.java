package io.kaif.mobile.event.article;

import io.kaif.mobile.model.Vote;

public class VoteDebateSuccessEvent extends ArticleEvent {
  private final String debateId;
  private final Vote.VoteState voteState;

  public VoteDebateSuccessEvent(String debateId, Vote.VoteState voteState) {
    this.debateId = debateId;
    this.voteState = voteState;
  }

  public String getDebateId() {
    return debateId;
  }

  public Vote.VoteState getVoteState() {
    return voteState;
  }

  @Override
  public String toString() {
    return "VoteArticleSuccessEvent{" +
        "debateId='" + debateId + '\'' +
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

    VoteDebateSuccessEvent that = (VoteDebateSuccessEvent) o;

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
