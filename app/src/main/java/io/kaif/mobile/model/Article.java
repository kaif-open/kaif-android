package io.kaif.mobile.model;

import java.io.Serializable;
import java.util.Date;

import android.os.Parcelable;
import auto.parcel.AutoParcel;
import io.kaif.mobile.json.AutoGson;

@AutoParcel
@AutoGson
public abstract class Article implements Parcelable, Serializable {

  public enum ArticleType {
    EXTERNAL_LINK, SPEAK
  }

  public abstract String zone();

  public abstract String zoneTitle();

  public abstract String articleId();

  public abstract String title();

  public abstract Date createTime();

  public abstract String link();

  public abstract String content();

  public abstract ArticleType articleType();

  public abstract String authorName();

  public abstract long upVote();

  public abstract long debateCount();

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Article) {
      Article that = (Article) o;
      return (this.articleId().equals(that.articleId()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= articleId().hashCode();
    return h;
  }

  public static Article of(String zone,
      String zoneTitle,
      String articleId,
      String title,
      Date createTime,
      String link,
      String content,
      Article.ArticleType articleType,
      String authorName,
      long upVote,
      long debateCount) {
    return new AutoParcel_Article(zone,
        zoneTitle,
        articleId,
        title,
        createTime,
        link,
        content,
        articleType,
        authorName,
        upVote,
        debateCount);
  }
}
