package io.kaif.mobile.event.article;

public class CreateDebateFailedEvent extends ArticleEvent {
  private String localId;

  public CreateDebateFailedEvent(String localId) {

    this.localId = localId;
  }

  public String getLocalId() {
    return localId;
  }
}
