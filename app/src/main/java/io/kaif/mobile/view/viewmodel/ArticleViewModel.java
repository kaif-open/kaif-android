package io.kaif.mobile.view.viewmodel;

import java.io.Serializable;
import java.util.Date;

import android.net.Uri;
import io.kaif.mobile.model.Article;
import io.kaif.mobile.model.Vote;

public class ArticleViewModel implements Serializable {

  private Article article;

  private Vote vote;

  private Vote.VoteState prevVoteState;

  private Vote.VoteState currentVoteState;

  private boolean canShowVoteAnimation;

  public ArticleViewModel(Article article, Vote vote) {
    this.article = article;
    this.vote = vote;
    this.prevVoteState = vote.getVoteState();
    this.currentVoteState = vote.getVoteState();
    this.canShowVoteAnimation = true;
  }

  public String getZone() {
    return article.getZone();
  }

  public Date getCreateTime() {
    return article.getCreateTime();
  }

  public String getTitle() {
    return article.getTitle();
  }

  public Article.ArticleType getArticleType() {
    return article.getArticleType();
  }

  public long getScore() {
    return article.getUpVote() + currentVoteState.delta(vote.getVoteState());
  }

  public String getLink() {
    return article.getLink();
  }

  public long getDebateCount() {
    return article.getDebateCount();
  }

  public String getZoneTitle() {
    return article.getZoneTitle();
  }

  public String getArticleId() {
    return article.getArticleId();
  }

  public String getContent() {
    return article.getContent();
  }

  public String getAuthorName() {
    return article.getAuthorName();
  }

  public void setCanShowVoteAnimation(boolean canShowVoteAnimation) {
    this.canShowVoteAnimation = canShowVoteAnimation;
  }

  public boolean shouldShowVoteEffect() {
    if (!canShowVoteAnimation) {
      return false;
    }

    if (currentVoteState == prevVoteState && currentVoteState == Vote.VoteState.EMPTY) {
      return false;
    }
    return true;
  }

  public void updateVoteState(Vote.VoteState voteState) {
    prevVoteState = currentVoteState;
    currentVoteState = voteState;
    canShowVoteAnimation = true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ArticleViewModel that = (ArticleViewModel) o;

    return article.equals(that.article);

  }

  @Override
  public int hashCode() {
    return article.hashCode();
  }

  public Vote.VoteState getCurrentVoeState() {
    return currentVoteState;
  }

  public Uri getPermaLink() {
    return new Uri.Builder().scheme("https")
        .authority("kaif.io")
        .appendPath("z")
        .appendPath(getZone())
        .appendPath("debates")
        .appendPath(getArticleId())
        .build();
  }
}
