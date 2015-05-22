package io.kaif.mobile.event.debate;

import io.kaif.mobile.model.LocalDebate;

public class CreateLocalDebateEvent extends DebateEvent {
  private LocalDebate localDebate;

  public CreateLocalDebateEvent(String articleId,
      String localDebateId,
      String parentDebateId,
      int level,
      String content) {
    this.localDebate = new LocalDebate(articleId, localDebateId, parentDebateId, level, content);
  }

  public LocalDebate getLocalDebate() {
    return localDebate;
  }
}
