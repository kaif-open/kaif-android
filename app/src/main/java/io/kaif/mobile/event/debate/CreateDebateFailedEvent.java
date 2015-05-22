package io.kaif.mobile.event.debate;

import io.kaif.mobile.event.article.ArticleEvent;

public class CreateDebateFailedEvent extends DebateEvent {
  private String localId;

  public CreateDebateFailedEvent(String localId) {

    this.localId = localId;
  }

  public String getLocalId() {
    return localId;
  }
}
