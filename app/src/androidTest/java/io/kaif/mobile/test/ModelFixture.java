package io.kaif.mobile.test;

import java.util.Date;
import java.util.UUID;

import io.kaif.mobile.model.Article;
import io.kaif.mobile.model.Debate;
import io.kaif.mobile.model.Vote;

public interface ModelFixture {

  default Article article(String id) {
    return new Article("programming",
        "pro",
        id,
        "aTitle",
        new Date(),
        "http://foo.com",
        "content",
        Article.ArticleType.EXTERNAL_LINK,
        "bar",
        0L,
        0L);
  }

  default Vote upVote(String id) {
    return new Vote(id, Vote.VoteState.UP, new Date());
  }
  default Vote downVote(String id) {
    return new Vote(id, Vote.VoteState.DOWN, new Date());
  }

  default Vote emptyVote(String id) {
    return new Vote(id, Vote.VoteState.EMPTY, new Date());
  }

  default Debate debate(String articleId, String debateId, String parentDebateId, int level) {
    return new Debate(articleId,
        debateId,
        "programming",
        parentDebateId,
        level,
        "content",
        "tester",
        0L,
        0L,
        new Date(),
        new Date());
  }
}
