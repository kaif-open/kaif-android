package io.kaif.mobile.model;

import java.io.Serializable;
import java.util.Date;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import auto.parcel.AutoParcel;
import io.kaif.mobile.json.AutoGson;

@AutoParcel
@AutoGson
public abstract class FeedAsset implements Parcelable, Serializable {
  enum AssetType {
    DEBATE_FROM_REPLY
  }

  public static FeedAsset of(String assetId,
      AssetType assetType,
      Date createTime,
      boolean acknowledged,
      Debate debate) {
    return new AutoParcel_FeedAsset(assetId, assetType, createTime, acknowledged, debate);
  }

  public abstract String assetId();

  public abstract AssetType assetType();

  public abstract Date createTime();

  public abstract boolean acknowledged();

  @Nullable
  public abstract Debate debate();

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof FeedAsset) {
      FeedAsset that = (FeedAsset) o;
      return (this.assetId().equals(that.assetId()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= assetId().hashCode();
    return h;
  }

}
