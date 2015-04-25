package io.kaif.mobile.model;

public class LocalDebate {

  private final String articleId;

  private final String localDebateId;

  private final String parentDebateId;

  private final int level;

  private final String content;

  public LocalDebate(String articleId,
      String localDebateId,
      String parentDebateId,
      int level,
      String content) {
    this.articleId = articleId;
    this.localDebateId = localDebateId;
    this.parentDebateId = parentDebateId;
    this.level = level;
    this.content = content;
  }

  public String getArticleId() {
    return articleId;
  }

  public String getLocalDebateId() {
    return localDebateId;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    LocalDebate that = (LocalDebate) o;

    return localDebateId.equals(that.localDebateId);

  }

  @Override
  public int hashCode() {
    return localDebateId.hashCode();
  }
}
