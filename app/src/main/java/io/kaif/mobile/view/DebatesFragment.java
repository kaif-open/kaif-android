package io.kaif.mobile.view;

import javax.inject.Inject;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import io.kaif.mobile.KaifApplication;
import io.kaif.mobile.R;
import io.kaif.mobile.app.BaseFragment;
import io.kaif.mobile.event.debate.CreateDebateFailedEvent;
import io.kaif.mobile.event.debate.CreateDebateSuccessEvent;
import io.kaif.mobile.event.debate.CreateLocalDebateEvent;
import io.kaif.mobile.event.debate.DebateEvent;
import io.kaif.mobile.event.vote.VoteArticleSuccessEvent;
import io.kaif.mobile.event.vote.VoteDebateSuccessEvent;
import io.kaif.mobile.view.daemon.DebateDaemon;
import io.kaif.mobile.view.daemon.VoteDaemon;
import io.kaif.mobile.view.util.Views;
import io.kaif.mobile.view.viewmodel.ArticleViewModel;
import io.kaif.mobile.view.widget.ReplyDialog;

public class DebatesFragment extends BaseFragment {

  public static final String ARTICLE = "ARTICLE";
  public static final String DEBATE_ID = "DEBATE_ID";
  public static final int AUTO_SCROLL_OFFSET_DP = 30;
  @InjectView(R.id.debate_list)
  RecyclerView debateListView;

  @InjectView(R.id.pull_to_refresh)
  SwipeRefreshLayout pullToRefreshLayout;

  @Inject
  DebateDaemon debateDaemon;

  @Inject
  VoteDaemon voteDaemon;

  private DebateListAdapter adapter;
  private ArticleViewModel article;

  public static DebatesFragment newInstance(ArticleViewModel articleViewModel,
      String anchorDebateId) {
    DebatesFragment fragment = new DebatesFragment();
    Bundle args = new Bundle();
    args.putSerializable(ARTICLE, articleViewModel);
    args.putString(DEBATE_ID, anchorDebateId);
    fragment.setArguments(args);
    return fragment;
  }

  public DebatesFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    KaifApplication.getInstance().beans().inject(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_debates, container, false);
    ButterKnife.inject(this, view);
    article = (ArticleViewModel) getArguments().getSerializable(ARTICLE);
    setupView();
    fillContent();
    return view;
  }

  private void fillContent() {
    refreshDebates();
  }

  private void setupView() {
    final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    debateListView.setLayoutManager(linearLayoutManager);
    adapter = new DebateListAdapter(article,
        voteDaemon,
        (debateId, level) -> ReplyDialog.createFragment(article.getArticleId(), debateId, level)
            .show(getFragmentManager(), "fragment_reply"));
    debateListView.setAdapter(adapter);
    debateListView.getItemAnimator().setChangeDuration(120);

    pullToRefreshLayout.setOnRefreshListener(this::refreshDebates);
    pullToRefreshLayout.setRefreshing(true);
    bind(voteDaemon.getSubject(VoteArticleSuccessEvent.class, VoteDebateSuccessEvent.class)).
        subscribe(event -> {
          if (event instanceof VoteArticleSuccessEvent) {
            VoteArticleSuccessEvent articleSuccessEvent = (VoteArticleSuccessEvent) event;
            adapter.updateArticleVote(articleSuccessEvent.getArticleId(),
                articleSuccessEvent.getVoteState());
          } else if (event instanceof VoteDebateSuccessEvent) {
            VoteDebateSuccessEvent debateSuccessEvent = (VoteDebateSuccessEvent) event;
            adapter.updateDebateVote(debateSuccessEvent.getDebateId(),
                debateSuccessEvent.getVoteState());
          }
        });

    bind(debateDaemon.getSubject(CreateLocalDebateEvent.class,
        CreateDebateSuccessEvent.class,
        CreateDebateFailedEvent.class)).subscribe(this::processDebateEvent);
  }

  private void processDebateEvent(DebateEvent event) {
    if (event instanceof CreateDebateSuccessEvent) {
      refreshDebates();
    }
  }

  public void refreshDebates() {
    bind(debateDaemon.listDebates(article.getArticleId())).subscribe((debates) -> {
      adapter.refresh(debates);
      String anchorId = getArguments().getString(DEBATE_ID);
      if (anchorId != null) {
        int position = adapter.findPositionByDebateId(anchorId);
        adapter.selectItem(position);
        ((LinearLayoutManager) debateListView.getLayoutManager()).scrollToPositionWithOffset(
            position,
            (int) Views.convertDpToPixel(AUTO_SCROLL_OFFSET_DP, getActivity()));
      }
    }, throwable -> {
    }, () -> pullToRefreshLayout.setRefreshing(false));
  }

}
