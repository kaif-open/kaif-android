package io.kaif.mobile.view.daemon;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

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
    return Observable.empty();
  }

  public Observable<List<FeedAssetViewModel>> listNewsFeed(String feedAssetId) {
    return Observable.empty();
  }
}
