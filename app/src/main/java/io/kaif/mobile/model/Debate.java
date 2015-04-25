package io.kaif.mobile.model;

import java.util.Date;

public class Debate {
  private final String articleId;

  private final String debateId;

  private final String zone;

  private final String parentDebateId;

  private final int level;

  private final String content;

  private final String debaterName;

  private final long upVote;

  private final long downVote;

  private final Date createTime;

  private final Date lastUpdateTime;

  public Debate(String articleId,
      String debateId,
      String zone,
      String parentDebateId,
      int level,
      String content,
      String debaterName,
      long upVote,
      long downVote,
      Date createTime,
      Date lastUpdateTime) {
    this.articleId = articleId;
    this.debateId = debateId;
    this.zone = zone;
    this.parentDebateId = parentDebateId;
    this.level = level;
    this.content = content;
    this.debaterName = debaterName;
    this.upVote = upVote;
    this.downVote = downVote;
    this.createTime = createTime;
    this.lastUpdateTime = lastUpdateTime;
  }

  @Override
  public String toString() {
    return "Debate{" +
        "articleId='" + articleId + '\'' +
        ", debateId='" + debateId + '\'' +
        ", zone='" + zone + '\'' +
        ", parentDebateId='" + parentDebateId + '\'' +
        ", level=" + level +
        ", content='" + content + '\'' +
        ", debaterName='" + debaterName + '\'' +
        ", upVote=" + upVote +
        ", downVote=" + downVote +
        ", createTime=" + createTime +
        ", lastUpdateTime=" + lastUpdateTime +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Debate debate = (Debate) o;

    return debateId.equals(debate.debateId);

  }

  @Override
  public int hashCode() {
    return debateId.hashCode();
  }

  public String getArticleId() {
    return articleId;
  }

  public String getDebateId() {
    return debateId;
  }

  public String getZone() {
    return zone;
  }

  public String getParentDebateId() {
    return parentDebateId;
  }

  public int getLevel() {
    return level;
  }

  public String getContent() {
    return content;
  }

  public String getDebaterName() {
    return debaterName;
  }

  public long getUpVote() {
    return upVote;
  }

  public long getDownVote() {
    return downVote;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public Date getLastUpdateTime() {
    return lastUpdateTime;
  }
}
