package io.kaif.mobile.model;

import java.io.Serializable;
import java.util.Date;

public class Article implements Serializable {

  public enum ArticleType {
    EXTERNAL_LINK, SPEAK
  }

  private final String zone;

  private final String zoneTitle;

  private final String articleId;

  private final String title;

  private final Date createTime;

  private final String link;

  private final String content;

  private final ArticleType articleType;

  private final String authorName;

  private final long upVote;

  private final long debateCount;

  public Article(String zone,
      String zoneTitle,
      String articleId,
      String title,
      Date createTime,
      String link,
      String content,
      ArticleType articleType,
      String authorName,
      long upVote,
      long debateCount) {
    this.zone = zone;
    this.zoneTitle = zoneTitle;
    this.articleId = articleId;
    this.title = title;
    this.createTime = createTime;
    this.link = link;
    this.content = content;
    this.articleType = articleType;
    this.authorName = authorName;
    this.upVote = upVote;
    this.debateCount = debateCount;
  }

  public String getZone() {
    return zone;
  }

  public String getZoneTitle() {
    return zoneTitle;
  }

  public String getArticleId() {
    return articleId;
  }

  public String getTitle() {
    return title;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public String getLink() {
    return link;
  }

  public String getContent() {
    return content;
  }

  public ArticleType getArticleType() {
    return articleType;
  }

  public String getAuthorName() {
    return authorName;
  }

  public long getUpVote() {
    return upVote;
  }

  public long getDebateCount() {
    return debateCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Article article = (Article) o;

    return articleId.equals(article.articleId);

  }

  @Override
  public int hashCode() {
    return articleId.hashCode();
  }

  @Override
  public String toString() {
    return "Article{" +
        "zone='" + zone + '\'' +
        ", zoneTitle='" + zoneTitle + '\'' +
        ", articleId='" + articleId + '\'' +
        ", title='" + title + '\'' +
        ", createTime=" + createTime +
        ", link='" + link + '\'' +
        ", content='" + content + '\'' +
        ", articleType=" + articleType +
        ", authorName='" + authorName + '\'' +
        ", upVote=" + upVote +
        ", debateCount=" + debateCount +
        '}';
  }
}