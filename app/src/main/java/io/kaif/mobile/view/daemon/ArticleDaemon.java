package io.kaif.mobile.view.daemon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import io.kaif.mobile.event.EventPublishSubject;
import io.kaif.mobile.event.article.ArticleEvent;
import io.kaif.mobile.model.Article;
import io.kaif.mobile.model.Vote;
import io.kaif.mobile.model.exception.DuplicateArticleUrlException;
import io.kaif.mobile.service.ArticleService;
import io.kaif.mobile.service.CommaSeparatedParam;
import io.kaif.mobile.service.VoteService;
import io.kaif.mobile.view.viewmodel.ArticleViewModel;
import rx.Observable;

@Singleton
public class ArticleDaemon {

  private final EventPublishSubject<ArticleEvent> subject;

  private final ArticleService articleService;

  private final VoteService voteService;

  public Observable<ArticleEvent> getSubject(Class<?>... classes) {
    return subject.getSubject(classes);
  }

  public <T extends ArticleEvent> Observable<T> getSubject(Class<T> clazz) {
    return subject.getSubject(clazz);
  }

  @Inject
  ArticleDaemon(ArticleService articleService, VoteService voteService) {
    this.voteService = voteService;
    this.articleService = articleService;
    this.subject = new EventPublishSubject<>();
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

  public Observable<ArticleViewModel> loadArticle(String articleId) {
    return articleService.loadArticle(articleId)
        .flatMap(article -> voteService.listArticleVotes(CommaSeparatedParam.of(Collections.singletonList(
            article.getArticleId())))
            .map(votes -> new Pair<>(Collections.singletonList(article), votes))
            .map(this::mapToViewModel)
            .map(articleViewModels -> articleViewModels.get(0)));
  }

  private Vote loadVote(List<Vote> votes, String targetId) {
    for (Vote vote : votes) {
      if (vote.matches(targetId)) {
        return vote;
      }
    }
    return Vote.abstain(targetId);
  }

  private List<String> mapArticlesToIds(List<Article> articles) {
    List<String> ids = new ArrayList<>();
    for (Article article : articles) {
      ids.add(article.getArticleId());
    }
    return ids;
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

}
