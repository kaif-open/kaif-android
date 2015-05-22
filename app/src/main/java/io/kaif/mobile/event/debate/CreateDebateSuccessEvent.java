package io.kaif.mobile.event.debate;

import io.kaif.mobile.event.article.ArticleEvent;
import io.kaif.mobile.model.Debate;

public class CreateDebateSuccessEvent extends DebateEvent {

  private String localId;

  private Debate debate;

  public CreateDebateSuccessEvent(String localId, Debate debate) {
    this.localId = localId;
    this.debate = debate;
  }

  public String getLocalId() {
    return localId;
  }

  public Debate getDebate() {
    return debate;
  }
}
