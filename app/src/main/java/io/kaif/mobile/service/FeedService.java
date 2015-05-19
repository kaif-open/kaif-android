package io.kaif.mobile.service;

import java.util.List;

import io.kaif.mobile.model.FeedAsset;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

public interface FeedService {

  class AcknowledgeEntry {
    String assetId;

    public AcknowledgeEntry(String assetId) {
      this.assetId = assetId;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      AcknowledgeEntry that = (AcknowledgeEntry) o;

      return assetId.equals(that.assetId);

    }

    @Override
    public int hashCode() {
      return assetId.hashCode();
    }
  }

  @POST("/v1/feed/acknowledge")
  void acknowledge(@Body AcknowledgeEntry acknowledgeEntry);

  @GET("/v1/feed/news")
  Observable<List<FeedAsset>> news();

  @GET("/v1/feed/news-unread-count")
  Observable<Integer> newsUnreadCount();

}
