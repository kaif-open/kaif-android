package io.kaif.mobile.view.daemon;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import io.kaif.mobile.event.article.ArticleEvent;
import io.kaif.mobile.event.article.CreateDebateFailedEvent;
import io.kaif.mobile.event.article.CreateDebateSuccessEvent;
import io.kaif.mobile.event.article.CreateLocalDebateEvent;
import io.kaif.mobile.event.article.VoteArticleSuccessEvent;
import io.kaif.mobile.event.article.VoteDebateSuccessEvent;
import io.kaif.mobile.model.Article;
import io.kaif.mobile.model.Debate;
import io.kaif.mobile.model.DebateNode;
import io.kaif.mobile.model.Vote;
import io.kaif.mobile.model.exception.DuplicateArticleUrlException;
import io.kaif.mobile.service.ArticleService;
import io.kaif.mobile.service.CommaSeparatedParam;
import io.kaif.mobile.service.DebateService;
import io.kaif.mobile.service.VoteService;
import io.kaif.mobile.view.viewmodel.ArticleViewModel;
import io.kaif.mobile.view.viewmodel.DebateViewModel;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

@Singleton
public class ArticleDaemon {

  private Context context;

  private final PublishSubject<ArticleEvent> subject;

  private final ArticleService articleService;

  private final VoteService voteService;

  private final DebateService debateService;

  public Observable<ArticleEvent> getSubject(Class<?>... classes) {
    return subject.asObservable().filter(articleEvent -> {
      for (Class<?> clazz : classes) {
        if (clazz.isInstance(articleEvent)) {
          return true;
        }
      }
      return false;
    }).observeOn(AndroidSchedulers.mainThread());
  }

  public <T extends ArticleEvent> Observable<T> getSubject(Class<T> clazz) {
    return subject.asObservable()
        .filter(clazz::isInstance)
        .cast(clazz)
        .observeOn(AndroidSchedulers.mainThread());
  }

  @Inject
  ArticleDaemon(Context context,
      ArticleService articleService,
      VoteService voteService,
      DebateService debateService) {
    this.context = context;
    this.voteService = voteService;
    this.articleService = articleService;
    this.debateService = debateService;
    this.subject = PublishSubject.<ArticleEvent>create();
  }

  public Observable<Article> createExternalLink(@NonNull String url,
      @NonNull String title,
      @NonNull String zone,
      boolean forceCreate) {
    Observable<Void> checkObservable = Observable.just(null);
    if (!forceCreate) {
      checkObservable = articleService.exist(zone, url).flatMap(duplicate -> {
        if (duplicate) {
          return Observable.error(new DuplicateArticleUrlException());
        }
        return Observable.just(null);
      });
    }

    return checkObservable.flatMap(aVoid -> articleService.createExternalLink(new ArticleService.ExternalLinkEntry(
        url,
        title,
        zone)));
  }

  public Observable<List<DebateViewModel>> listLatestDebates(String startDebateId) {
    return debateService.listLatestDebates(startDebateId).flatMap(debates -> {
      final List<String> ids = mapDebatesToIds(debates);
      return voteService.listDebateVotes(CommaSeparatedParam.of(ids))
          .map(votes -> new Pair<>(debates, votes))
          .map(this::mapToDebateViewModel);
    });
  }

  public Observable<List<ArticleViewModel>> listHotArticles(String startArticleId) {
    return articleService.listHotArticles(startArticleId).flatMap(articles -> {
      List<String> ids = mapArticlesToIds(articles);
      return voteService.listArticleVotes(CommaSeparatedParam.of(ids))
          .map(votes -> new Pair<>(articles, votes))
          .map(this::mapToViewModel);
    });
  }

  public Observable<List<ArticleViewModel>> listLatestArticles(String startArticleId) {
    return articleService.listLatestArticles(startArticleId).flatMap(articles -> {
      List<String> ids = mapArticlesToIds(articles);
      return voteService.listArticleVotes(CommaSeparatedParam.of(ids))
          .map(votes -> new Pair<>(articles, votes))
          .map(this::mapToViewModel);
    });
  }

  private List<ArticleViewModel> mapToViewModel(Pair<List<Article>, List<Vote>> articlesAndVotes) {
    final List<Article> articles = articlesAndVotes.first;
    final List<Vote> votes = articlesAndVotes.second;

    List<ArticleViewModel> viewModels = new ArrayList<>();
    for (Article article : articles) {
      viewModels.add(new ArticleViewModel(article, loadVote(votes, article.getArticleId())));
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

  public void voteDebate(String debateId, Vote.VoteState prevState, Vote.VoteState voteState) {
    subject.onNext(new VoteDebateSuccessEvent(debateId, voteState));
    voteService.voteDebate(new VoteService.VoteDebateEntry(debateId, voteState))
        .subscribe(aVoid -> {
          //success do nothing
        }, throwable -> {
          subject.onNext(new VoteDebateSuccessEvent(debateId, prevState));
        });
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

  public Observable<List<DebateViewModel>> listDebates(String articleId) {
    return debateService.getDebateTree(articleId)
        .map(this::mapDebateTreeToList)
        .flatMap(debates -> {
          final List<String> ids = mapDebatesToIds(debates);
          return voteService.listDebateVotes(CommaSeparatedParam.of(ids))
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

  private List<String> mapArticlesToIds(List<Article> articles) {
    List<String> ids = new ArrayList<>();
    for (Article article : articles) {
      ids.add(article.getArticleId());
    }
    return ids;
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
