package io.kaif.mobile.view.viewmodel;

import io.kaif.mobile.model.FeedAsset;
import io.kaif.mobile.model.Vote;

public class FeedAssetViewModel {

  private DebateViewModel debateViewModel;
  private String assetId;

  public FeedAssetViewModel(FeedAsset feedAsset) {
    this.assetId = feedAsset.getAssetId();
    this.debateViewModel = new DebateViewModel(feedAsset.getDebate(),
        Vote.abstain(feedAsset.getDebate().getDebateId()));
  }

  public DebateViewModel getDebateViewModel() {
    return debateViewModel;
  }

  public String getAssetId() {
    return assetId;
  }
}
