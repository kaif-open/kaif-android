package io.kaif.mobile.view.daemon;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.kaif.mobile.IgnoreAllSubscriber;
import io.kaif.mobile.model.FeedAsset;
import io.kaif.mobile.service.FeedService;
import io.kaif.mobile.view.viewmodel.FeedAssetViewModel;
import rx.Observable;

@Singleton
public class FeedDaemon {

  private final FeedService feedService;

  @Inject
  FeedDaemon(FeedService feedService) {
    this.feedService = feedService;
  }

  public Observable<Integer> newsUnreadCount() {
    return feedService.newsUnreadCount();
  }

  public Observable<List<FeedAssetViewModel>> listAndAcknowledgeIfRequired() {
    return feedService.news(null).map(feedAssets -> {
      if (!feedAssets.isEmpty()) {
        feedService.acknowledge(new FeedService.AcknowledgeEntry(feedAssets.get(0).getAssetId()))
            .subscribe(new IgnoreAllSubscriber<>());
      }
      return mapToViewModel(feedAssets);
    });
  }

  private List<FeedAssetViewModel> mapToViewModel(List<FeedAsset> feedAssets) {
    List<FeedAssetViewModel> vms = new ArrayList<>();
    boolean isRead = false;
    for (int i = 0; i < feedAssets.size(); i++) {
      FeedAsset feedAsset = feedAssets.get(i);
      isRead |= feedAsset.isAcknowledged();
      vms.add(new FeedAssetViewModel(feedAsset, isRead));
    }
    return vms;
  }

  public Observable<List<FeedAssetViewModel>> listNewsFeed(String feedAssetId) {
    return feedService.news(feedAssetId).map(this::mapToViewModel);
  }
}
