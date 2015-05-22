package io.kaif.mobile.view.daemon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.support.v4.util.Pair;
import io.kaif.mobile.event.EventPublishSubject;
import io.kaif.mobile.event.debate.CreateDebateFailedEvent;
import io.kaif.mobile.event.debate.CreateDebateSuccessEvent;
import io.kaif.mobile.event.debate.CreateLocalDebateEvent;
import io.kaif.mobile.event.debate.DebateEvent;
import io.kaif.mobile.model.Debate;
import io.kaif.mobile.model.DebateNode;
import io.kaif.mobile.model.Vote;
import io.kaif.mobile.service.CommaSeparatedParam;
import io.kaif.mobile.service.DebateService;
import io.kaif.mobile.service.VoteService;
import io.kaif.mobile.view.viewmodel.DebateViewModel;
import rx.Observable;

@Singleton
public class DebateDaemon {

  private final EventPublishSubject<DebateEvent> subject;

  public EventPublishSubject<DebateEvent> subject() {
    return subject;
  }

  private final DebateService debateService;

  private final VoteService voteService;

  public Observable<DebateEvent> getSubject(Class<?>... classes) {
    return subject.getSubject(classes);
  }

  public <T extends DebateEvent> Observable<T> getSubject(Class<T> clazz) {
    return subject.getSubject(clazz);
  }

  @Inject
  DebateDaemon(DebateService debateService, VoteService voteService) {
    this.debateService = debateService;
    this.voteService = voteService;
    subject = new EventPublishSubject<>();
  }

  public Observable<List<DebateViewModel>> listLatestDebates(String startDebateId) {
    return debateService.listLatestDebates(startDebateId).flatMap(debates -> {
      final List<String> ids = mapDebatesToIds(debates);
      return voteService.listDebateVotes(CommaSeparatedParam.of(ids))
          .onErrorReturn(throwable -> Collections.emptyList())
          .map(votes -> new Pair<>(debates, votes))
          .map(this::mapToDebateViewModel);
    });
  }

  public Observable<List<DebateViewModel>> listDebates(String articleId) {
    return debateService.getDebateTree(articleId)
        .map(this::mapDebateTreeToList)
        .flatMap(debates -> {
          final List<String> ids = mapDebatesToIds(debates);
          return voteService.listDebateVotes(CommaSeparatedParam.of(ids))
              .onErrorReturn(throwable -> Collections.emptyList())
              .map(votes -> new Pair<>(debates, votes))
              .map(this::mapToDebateViewModel);
        });
  }

  private List<DebateViewModel> mapToDebateViewModel(Pair<List<Debate>, List<Vote>> debatesAndVotes) {
    final List<Debate> debates = debatesAndVotes.first;
    final List<Vote> votes = debatesAndVotes.second;
    List<DebateViewModel> viewModels = new ArrayList<>();
    for (Debate debate : debates) {
      viewModels.add(new DebateViewModel(debate, loadVote(votes, debate.getDebateId())));
    }
    return viewModels;
  }

  private Vote loadVote(List<Vote> votes, String targetId) {
    for (Vote vote : votes) {
      if (vote.matches(targetId)) {
        return vote;
      }
    }
    return Vote.abstain(targetId);
  }

  private List<String> mapDebatesToIds(List<Debate> debates) {
    List<String> ids = new ArrayList<>();
    for (Debate debate : debates) {
      ids.add(debate.getDebateId());
    }
    return ids;
  }

  private List<Debate> mapDebateTreeToList(DebateNode debateNode) {
    List<Debate> debates = new ArrayList<>();
    if (debateNode.getDebate() != null) {
      debates.add(debateNode.getDebate());
    }
    for (DebateNode child : debateNode.getChildren()) {
      debates.addAll(mapDebateTreeToList(child));
    }
    return debates;
  }

  public void debate(String articleId, String parentDebateId, int level, String content) {
    final String localId = UUID.randomUUID().toString();
    subject.onNext(new CreateLocalDebateEvent(articleId, localId, parentDebateId, level, content));
    debateService.debate(new DebateService.CreateDebateEntry(articleId, parentDebateId, content))
        .subscribe(debate -> subject.onNext(new CreateDebateSuccessEvent(localId, debate)),
            throwable -> subject.onNext(new CreateDebateFailedEvent(localId)));
  }
}
