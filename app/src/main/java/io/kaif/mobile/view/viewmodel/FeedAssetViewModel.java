package io.kaif.mobile.view.viewmodel;

import io.kaif.mobile.model.FeedAsset;
import io.kaif.mobile.model.Vote;

public class FeedAssetViewModel {

  private DebateViewModel debateViewModel;
  private FeedAsset feedAsset;
  private boolean read;

  public FeedAssetViewModel(FeedAsset feedAsset, boolean read) {
    this.feedAsset = feedAsset;
    this.read = read;
    //doesn't support inline vote yet, provide fake vote state.
    this.debateViewModel = new DebateViewModel(feedAsset.getDebate(),
        Vote.abstain(feedAsset.getDebate().getDebateId()));
  }

  public DebateViewModel getDebateViewModel() {
    return debateViewModel;
  }

  public String getAssetId() {
    return feedAsset.getAssetId();
  }

  public boolean isRead() {
    return read;
  }
}
