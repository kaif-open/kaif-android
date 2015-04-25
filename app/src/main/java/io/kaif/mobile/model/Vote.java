package io.kaif.mobile.model;

import java.io.Serializable;
import java.util.Date;

public class Vote implements Serializable {
  public enum VoteState {
    UP(1), DOWN(-1), EMPTY(0);

    private int score;

    VoteState(int score) {
      this.score = score;
    }

    public int delta(VoteState prevVoteState) {
      return score - prevVoteState.score;
    }
  }

  private final String targetId;
  private final VoteState voteState;
  private final Date updateTime;

  public Vote(String targetId, VoteState voteState, Date updateTime) {
    this.targetId = targetId;
    this.voteState = voteState;
    this.updateTime = updateTime;
  }

  public boolean matches(String targetId) {
    return this.targetId.equals(targetId);
  }

  public VoteState getVoteState() {
    return voteState;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Vote vote = (Vote) o;

    if (!targetId.equals(vote.targetId)) {
      return false;
    }
    if (voteState != vote.voteState) {
      return false;
    }
    return updateTime.equals(vote.updateTime);

  }

  @Override
  public int hashCode() {
    int result = targetId.hashCode();
    result = 31 * result + voteState.hashCode();
    result = 31 * result + updateTime.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "Vote{" +
        "targetId='" + targetId + '\'' +
        ", voteState=" + voteState +
        ", updateTime=" + updateTime +
        '}';
  }

  public static Vote abstain(String id) {
    return new Vote(id, VoteState.EMPTY, new Date(0));
  }
}
