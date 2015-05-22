package io.kaif.mobile.view.daemon;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.test.AndroidTestCase;
import io.kaif.mobile.event.vote.VoteArticleSuccessEvent;
import io.kaif.mobile.event.vote.VoteDebateSuccessEvent;
import io.kaif.mobile.model.Vote;
import io.kaif.mobile.service.VoteService;
import io.kaif.mobile.test.ModelFixture;
import rx.Observable;

public class VoteDaemonTest extends AndroidTestCase implements ModelFixture {
  @Mock
  private VoteService mockVoteService;

  private VoteDaemon daemon;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    MockitoAnnotations.initMocks(this);
    daemon = new VoteDaemon(mockVoteService);
  }

  public void testVoteArticle() throws InterruptedException {
    final Observable<Void> expected = Observable.just(null);
    when(mockVoteService.voteArticle(new VoteService.VoteArticleEntry("aId",
        Vote.VoteState.UP))).thenReturn(expected);

    AtomicReference<VoteArticleSuccessEvent> ref = new AtomicReference<>();
    CountDownLatch latch = new CountDownLatch(1);
    daemon.getSubject(VoteArticleSuccessEvent.class)
        .cast(VoteArticleSuccessEvent.class)
        .subscribe(articleEvent -> {
          ref.set(articleEvent);
          latch.countDown();
        });

    daemon.voteArticle("aId", Vote.VoteState.EMPTY, Vote.VoteState.UP);
    latch.await(3, TimeUnit.SECONDS);
    assertEquals(new VoteArticleSuccessEvent("aId", Vote.VoteState.UP), ref.get());
  }

  public void testVoteDebate() throws InterruptedException {
    final Observable<Void> expected = Observable.just(null);
    when(mockVoteService.voteDebate(new VoteService.VoteDebateEntry("aId",
        Vote.VoteState.UP))).thenReturn(expected);

    AtomicReference<VoteDebateSuccessEvent> ref = new AtomicReference<>();
    CountDownLatch latch = new CountDownLatch(1);
    daemon.getSubject(VoteDebateSuccessEvent.class)
        .cast(VoteDebateSuccessEvent.class)
        .subscribe(debateSuccessEvent -> {
          ref.set(debateSuccessEvent);
          latch.countDown();
        });

    daemon.voteDebate("aId", Vote.VoteState.EMPTY, Vote.VoteState.UP);
    latch.await(3, TimeUnit.SECONDS);
    assertEquals(new VoteDebateSuccessEvent("aId", Vote.VoteState.UP), ref.get());
  }

  public void testVoteArticle_failed() throws InterruptedException {
    final Observable<Void> expected = Observable.error(new IOException());
    when(mockVoteService.voteArticle(new VoteService.VoteArticleEntry("aId",
        Vote.VoteState.UP))).thenReturn(expected);

    AtomicReference<VoteArticleSuccessEvent> ref = new AtomicReference<>();
    CountDownLatch latch = new CountDownLatch(2);
    daemon.getSubject(VoteArticleSuccessEvent.class)
        .cast(VoteArticleSuccessEvent.class)
        .subscribe(articleEvent -> {
          ref.set(articleEvent);
          latch.countDown();
        });

    daemon.voteArticle("aId", Vote.VoteState.EMPTY, Vote.VoteState.UP);

    latch.await(3, TimeUnit.SECONDS);
    assertEquals(new VoteArticleSuccessEvent("aId", Vote.VoteState.EMPTY), ref.get());
  }

  public void testVoteDebate_failed() throws InterruptedException {
    final Observable<Void> expected = Observable.error(new IOException());
    when(mockVoteService.voteDebate(new VoteService.VoteDebateEntry("aId",
        Vote.VoteState.UP))).thenReturn(expected);

    AtomicReference<VoteDebateSuccessEvent> ref = new AtomicReference<>();
    CountDownLatch latch = new CountDownLatch(2);
    daemon.getSubject(VoteDebateSuccessEvent.class).subscribe(event -> {
      ref.set(event);
      latch.countDown();
    });

    daemon.voteDebate("aId", Vote.VoteState.EMPTY, Vote.VoteState.UP);

    latch.await(3, TimeUnit.SECONDS);
    assertEquals(new VoteDebateSuccessEvent("aId", Vote.VoteState.EMPTY), ref.get());
  }

}
