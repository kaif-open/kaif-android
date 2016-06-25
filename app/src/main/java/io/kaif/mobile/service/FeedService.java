package io.kaif.mobile.service;

import java.util.List;

import io.kaif.mobile.model.FeedAsset;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
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
  Observable<Void> acknowledge(@Body AcknowledgeEntry acknowledgeEntry);

  @GET("/v1/feed/news")
  Observable<List<FeedAsset>> news(@Query("start-asset-id") String startAssetId);

  @GET("/v1/feed/news-unread-count")
  Observable<Integer> newsUnreadCount();

}
