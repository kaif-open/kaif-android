package io.kaif.mobile.view.daemon;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.test.AndroidTestCase;
import io.kaif.mobile.model.Article;
import io.kaif.mobile.model.Vote;
import io.kaif.mobile.model.exception.DuplicateArticleUrlException;
import io.kaif.mobile.service.ArticleService;
import io.kaif.mobile.service.CommaSeparatedParam;
import io.kaif.mobile.service.DebateService;
import io.kaif.mobile.service.VoteService;
import io.kaif.mobile.test.ModelFixture;
import io.kaif.mobile.view.viewmodel.ArticleViewModel;
import rx.Observable;

public class ArticleDaemonTest extends AndroidTestCase implements ModelFixture {

  @Mock
  private ArticleService mockArticleService;

  @Mock
  private VoteService mockVoteService;

  @Mock
  private DebateService mockDebateService;

  private ArticleDaemon daemon;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    MockitoAnnotations.initMocks(this);
    daemon = new ArticleDaemon(mockArticleService, mockVoteService);
  }

  public void testListHotArticles() {
    final List<Article> articles = asList(article("aId"), article("bId"), article("cId"));
    when(mockArticleService.listHotArticles(null)).thenReturn(Observable.just(articles));

    when(mockVoteService.listArticleVotes(CommaSeparatedParam.of(Arrays.asList("aId",
        "bId",
        "cId")))).thenReturn(Observable.just(asList(upVote("aId"), emptyVote("bId"))));

    final List<ArticleViewModel> result = daemon.listHotArticles(null).toBlocking().single();
    final ArticleViewModel aArticle = result.get(0);
    assertEquals("aId", aArticle.getArticleId());
    assertEquals("bar", aArticle.getAuthorName());
    assertEquals("content", aArticle.getContent());
    assertEquals("http://foo.com", aArticle.getLink());
    assertEquals("programming", aArticle.getZone());
    assertEquals("pro", aArticle.getZoneTitle());
    assertEquals("aTitle", aArticle.getTitle());
    assertEquals(Article.ArticleType.EXTERNAL_LINK, aArticle.getArticleType());
    assertEquals(Vote.VoteState.UP, aArticle.getCurrentVoeState());
    assertEquals(0L, aArticle.getDebateCount());
    assertEquals(0L, aArticle.getScore());
    assertNotNull(aArticle.getCreateTime());

    assertEquals(Vote.VoteState.EMPTY, result.get(1).getCurrentVoeState());
    assertEquals(Vote.VoteState.EMPTY, result.get(2).getCurrentVoeState());

  }

  public void testCreateExternalLink_duplicate() {
    when(mockArticleService.exist("zone", "http://example.com")).thenReturn(Observable.just(true));
    try {
      daemon.createExternalLink("http://example.com", "title", "zone", false).toBlocking().single();
      fail("expect duplicate post exception");
    } catch (Exception e) {
      assertTrue(e.getCause() instanceof DuplicateArticleUrlException);
    }
  }

  public void testCreateExternalLink_force_create() {

    Article article = new Article("zone",
        "pro",
        UUID.randomUUID().toString(),
        "title",
        new Date(),
        "http://example.com",
        "content",
        Article.ArticleType.EXTERNAL_LINK,
        "bar",
        0L,
        0L);

    when(mockArticleService.exist("zone", "http://example.com")).thenReturn(Observable.just(true));
    when(mockArticleService.createExternalLink(new ArticleService.ExternalLinkEntry(
        "http://example.com",
        "title",
        "zone"))).thenReturn(Observable.just(article));

    Article result = daemon.createExternalLink("http://example.com", "title", "zone", true)
        .toBlocking()
        .single();

    assertEquals(article.getArticleId(), result.getArticleId());
    assertEquals(article.getTitle(), result.getTitle());
    assertEquals(article.getZone(), result.getZone());
    assertEquals(article.getLink(), result.getLink());
  }

  public void testCreateExternalLink() {

    Article article = new Article("zone",
        "pro",
        UUID.randomUUID().toString(),
        "title",
        new Date(),
        "http://example.com",
        "content",
        Article.ArticleType.EXTERNAL_LINK,
        "bar",
        0L,
        0L);

    when(mockArticleService.exist("zone", "http://example.com")).thenReturn(Observable.just(false));
    when(mockArticleService.createExternalLink(new ArticleService.ExternalLinkEntry(
        "http://example.com",
        "title",
        "zone"))).thenReturn(Observable.just(article));

    Article result = daemon.createExternalLink("http://example.com", "title", "zone", false)
        .toBlocking()
        .single();

    assertEquals(article.getArticleId(), result.getArticleId());
  }

}