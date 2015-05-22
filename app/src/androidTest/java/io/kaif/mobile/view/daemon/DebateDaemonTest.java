package io.kaif.mobile.view.daemon;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.test.AndroidTestCase;
import io.kaif.mobile.event.debate.CreateDebateFailedEvent;
import io.kaif.mobile.event.debate.CreateDebateSuccessEvent;
import io.kaif.mobile.event.debate.CreateLocalDebateEvent;
import io.kaif.mobile.model.Debate;
import io.kaif.mobile.model.DebateNode;
import io.kaif.mobile.model.LocalDebate;
import io.kaif.mobile.model.Vote;
import io.kaif.mobile.service.CommaSeparatedParam;
import io.kaif.mobile.service.DebateService;
import io.kaif.mobile.service.VoteService;
import io.kaif.mobile.test.ModelFixture;
import io.kaif.mobile.view.viewmodel.DebateViewModel;
import rx.Observable;

public class DebateDaemonTest extends AndroidTestCase implements ModelFixture {

  @Mock
  private VoteService mockVoteService;

  @Mock
  private DebateService mockDebateService;

  private DebateDaemon daemon;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    MockitoAnnotations.initMocks(this);
    daemon = new DebateDaemon(mockDebateService, mockVoteService);
  }

  public void testListDebates() {

    final String articleId = "aId";
    DebateNode d111 = new DebateNode(debate(articleId, "d111", "d11", 3), emptyList());
    DebateNode d11 = new DebateNode(debate(articleId, "d11", "d1", 2), singletonList(d111));
    DebateNode d12 = new DebateNode(debate(articleId, "d12", "d1", 2), emptyList());
    DebateNode d21 = new DebateNode(debate(articleId, "d21", "d2", 2), emptyList());
    DebateNode d1 = new DebateNode(debate(articleId, "d1", null, 1), asList(d11, d12));
    DebateNode d2 = new DebateNode(debate(articleId, "d2", null, 1), singletonList(d21));
    DebateNode root = new DebateNode(null, asList(d1, d2));

    when(mockDebateService.getDebateTree("aId")).thenReturn(Observable.just(root));
    when(mockVoteService.listDebateVotes(CommaSeparatedParam.of(Arrays.asList("d1",
        "d11",
        "d111",
        "d12",
        "d2",
        "d21")))).thenReturn(Observable.just(asList(upVote("d1"),
        emptyVote("d111"),
        downVote("d2"))));

    final List<DebateViewModel> result = daemon.listDebates("aId").toBlocking().single();

    final DebateViewModel firstDebate = result.get(0);
    assertEquals("aId", firstDebate.getArticleId());
    assertEquals("content", firstDebate.getContent());
    assertNotNull(firstDebate.getCreateTime());
    assertEquals("d1", firstDebate.getDebateId());
    assertEquals("tester", firstDebate.getDebaterName());
    assertEquals(0L, firstDebate.getDownVote());
    assertNotNull(firstDebate.getLastUpdateTime());
    assertEquals(1, firstDebate.getLevel());
    assertNull(firstDebate.getParentDebateId());
    assertEquals(0L, firstDebate.getUpVote());
    assertEquals("programming", firstDebate.getZone());
    assertEquals(Vote.VoteState.UP, firstDebate.getCurrentVoeState());

    assertEquals(2, result.get(1).getLevel());
    assertEquals("d1", result.get(1).getParentDebateId());
    assertEquals(Vote.VoteState.EMPTY, result.get(2).getCurrentVoeState());
    assertEquals(Vote.VoteState.DOWN, result.get(4).getCurrentVoeState());

  }

  public void testDebates() throws InterruptedException {
    when(mockDebateService.debate(new DebateService.CreateDebateEntry("aId",
        "pId",
        "test"))).thenReturn(Observable.just(new Debate("aId",
        "debateId",
        "zone",
        "pId",
        1,
        "test",
        "fooName",
        0L,
        0L,
        new Date(),
        new Date())));

    Future<CreateLocalDebateEvent> f1 = Future.create(daemon.getSubject(CreateLocalDebateEvent.class));
    Future<CreateDebateSuccessEvent> f2 = Future.create(daemon.getSubject(CreateDebateSuccessEvent.class));

    daemon.debate("aId", "pId", 1, "test");

    LocalDebate localDebate = f1.await(3, TimeUnit.SECONDS).getLocalDebate();
    assertNotNull(localDebate.getLocalDebateId());
    assertEquals("aId", localDebate.getArticleId());
    assertEquals("test", localDebate.getContent());
    assertEquals(1, localDebate.getLevel());
    assertEquals("pId", localDebate.getParentDebateId());

    Debate debate = f2.await(3, TimeUnit.SECONDS).getDebate();

    assertEquals("debateId", debate.getDebateId());
    assertEquals("aId", debate.getArticleId());
    assertEquals("zone", debate.getZone());
    assertEquals("pId", debate.getParentDebateId());
    assertEquals(1, debate.getLevel());
    assertEquals("test", debate.getContent());
    assertEquals("fooName", debate.getDebaterName());
    assertEquals(0L, debate.getUpVote());
    assertEquals(0L, debate.getDownVote());
  }

  public void testDebates_failed() throws InterruptedException {
    when(mockDebateService.debate(new DebateService.CreateDebateEntry("aId",
        "pId",
        "test"))).thenReturn(Observable.error(new IOException("failed")));

    Future<CreateLocalDebateEvent> f1 = Future.create(daemon.getSubject(CreateLocalDebateEvent.class));
    Future<CreateDebateFailedEvent> f2 = Future.create(daemon.getSubject(CreateDebateFailedEvent.class));

    daemon.debate("aId", "pId", 1, "test");
    LocalDebate localDebate = f1.await(3, TimeUnit.SECONDS).getLocalDebate();
    String localId = f2.await(3, TimeUnit.SECONDS).getLocalId();
    assertEquals(localDebate.getLocalDebateId(), localId);
  }

  private static class Future<T> {
    AtomicReference<T> ref;
    CountDownLatch latch;

    static <T> Future<T> create(Observable<T> observable) {
      AtomicReference<T> ref = new AtomicReference<>();
      CountDownLatch latch = new CountDownLatch(1);
      observable.subscribe(event -> {
        ref.set(event);
        latch.countDown();
      });
      return new Future<>(latch, ref);
    }

    public Future(CountDownLatch latch, AtomicReference<T> ref) {
      this.latch = latch;
      this.ref = ref;
    }

    public T await(long timeout, TimeUnit unit) throws InterruptedException {
      if (latch.await(timeout, unit)) {
        return ref.get();
      }
      return null;
    }
  }

}