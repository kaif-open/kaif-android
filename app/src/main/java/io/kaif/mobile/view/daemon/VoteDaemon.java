package io.kaif.mobile.view.daemon;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.kaif.mobile.event.EventPublishSubject;
import io.kaif.mobile.event.vote.VoteArticleSuccessEvent;
import io.kaif.mobile.event.vote.VoteDebateSuccessEvent;
import io.kaif.mobile.event.vote.VoteEvent;
import io.kaif.mobile.model.Vote;
import io.kaif.mobile.service.VoteService;
import rx.Observable;

@Singleton
public class VoteDaemon {

  private final EventPublishSubject<VoteEvent> subject;

  private final VoteService voteService;

  public Observable<VoteEvent> getSubject(Class<?>... classes) {
    return subject.getSubject(classes);
  }

  public <T extends VoteEvent> Observable<T> getSubject(Class<T> clazz) {
    return subject.getSubject(clazz);
  }

  @Inject
  VoteDaemon(VoteService voteService) {
    this.voteService = voteService;
    this.subject = new EventPublishSubject<>();
  }

  public void voteArticle(String articleId, Vote.VoteState prevState, Vote.VoteState voteState) {
    subject.onNext(new VoteArticleSuccessEvent(articleId, voteState));
    voteService.voteArticle(new VoteService.VoteArticleEntry(articleId, voteState))
        .subscribe(aVoid -> {
          //success do nothing
        }, throwable -> {
          subject.onNext(new VoteArticleSuccessEvent(articleId, prevState));
        });
  }

  public void voteDebate(String debateId, Vote.VoteState prevState, Vote.VoteState voteState) {
    subject.onNext(new VoteDebateSuccessEvent(debateId, voteState));
    voteService.voteDebate(new VoteService.VoteDebateEntry(debateId, voteState))
        .subscribe(aVoid -> {
          //success do nothing
        }, throwable -> {
          subject.onNext(new VoteDebateSuccessEvent(debateId, prevState));
        });
  }

}
