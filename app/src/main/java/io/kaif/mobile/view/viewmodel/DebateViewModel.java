package io.kaif.mobile.view.viewmodel;

import java.util.Date;

import io.kaif.mobile.model.Debate;
import io.kaif.mobile.model.Vote;

public class DebateViewModel {

  private final Debate debate;

  private final Vote vote;

  private Vote.VoteState prevVoteState;

  private Vote.VoteState currentVoteState;

  private boolean canShowVoteAnimation;

  public DebateViewModel(Debate debate, Vote vote) {
    this.debate = debate;
    this.vote = vote;
    this.prevVoteState = vote.getVoteState();
    this.currentVoteState = vote.getVoteState();
    canShowVoteAnimation = true;
  }

  public long getDownVote() {
    return debate.getDownVote();
  }

  public String getDebaterName() {
    return debate.getDebaterName();
  }

  public int getLevel() {
    return debate.getLevel();
  }

  public Date getLastUpdateTime() {
    return debate.getLastUpdateTime();
  }

  public String getDebateId() {
    return debate.getDebateId();
  }

  public String getParentDebateId() {
    return debate.getParentDebateId();
  }

  public String getZone() {
    return debate.getZone();
  }

  public long getUpVote() {
    return debate.getUpVote();
  }

  public String getContent() {
    return debate.getContent();
  }

  public String getArticleId() {
    return debate.getArticleId();
  }

  public Date getCreateTime() {
    return debate.getCreateTime();
  }

  public Vote.VoteState getCurrentVoeState() {
    return currentVoteState;
  }

  public long getVoteScore() {
    return debate.getUpVote() - debate.getDownVote()
        + (currentVoteState.delta(vote.getVoteState()));
  }

  public void setCanShowVoteAnimation(boolean canShowVoteAnimation) {
    this.canShowVoteAnimation = canShowVoteAnimation;
  }

  public boolean shouldShowVoteEffect() {
    if (!canShowVoteAnimation) {
      return false;
    }
    if (currentVoteState == prevVoteState) {
      return false;
    }
    return true;
  }

  public void updateVoteState(Vote.VoteState voteState) {
    prevVoteState = this.currentVoteState;
    currentVoteState = voteState;
    canShowVoteAnimation = true;
  }

  public Vote.VoteState getPrevVoteState() {
    return prevVoteState;
  }
}
