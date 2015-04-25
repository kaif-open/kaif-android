package io.kaif.mobile.view.widget;

import io.kaif.mobile.model.Vote;

public interface OnVoteClickListener {
  void onVoteClicked(Vote.VoteState from, Vote.VoteState to);
}
