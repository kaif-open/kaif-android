package io.kaif.mobile.model;

import java.io.Serializable;
import java.util.Date;

import android.support.annotation.Nullable;

public class FeedAsset implements Serializable {
  enum AssetType {
    DEBATE_FROM_REPLY
  }

  private String assetId;

  private AssetType assetType;

  private Date createTime;

  private boolean acknowledged;

  @Nullable
  private Debate debate;

  public FeedAsset(String assetId,
      AssetType assetType,
      Date createTime,
      boolean acknowledged,
      @Nullable Debate debate) {
    this.assetId = assetId;
    this.assetType = assetType;
    this.createTime = createTime;
    this.acknowledged = acknowledged;
    this.debate = debate;
  }

  public String getAssetId() {
    return assetId;
  }

  public AssetType getAssetType() {
    return assetType;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public boolean isAcknowledged() {
    return acknowledged;
  }

  @Nullable
  public Debate getDebate() {
    return debate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    FeedAsset feedAsset = (FeedAsset) o;

    return assetId.equals(feedAsset.assetId);

  }

  @Override
  public int hashCode() {
    return assetId.hashCode();
  }
}
