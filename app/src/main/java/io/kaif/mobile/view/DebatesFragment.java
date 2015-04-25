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
import io.kaif.mobile.event.article.ArticleEvent;
import io.kaif.mobile.event.article.CreateDebateFailedEvent;
import io.kaif.mobile.event.article.CreateDebateSuccessEvent;
import io.kaif.mobile.event.article.CreateLocalDebateEvent;
import io.kaif.mobile.event.article.VoteArticleSuccessEvent;
import io.kaif.mobile.event.article.VoteDebateSuccessEvent;
import io.kaif.mobile.view.daemon.ArticleDaemon;
import io.kaif.mobile.view.viewmodel.ArticleViewModel;
import io.kaif.mobile.view.widget.ReplyDialog;

public class DebatesFragment extends BaseFragment {

  @InjectView(R.id.debate_list)
  RecyclerView debateListView;

  @InjectView(R.id.pull_to_refresh)
  SwipeRefreshLayout pullToRefreshLayout;

  @Inject
  ArticleDaemon articleDaemon;
  private DebateListAdapter adapter;
  private ArticleViewModel article;

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
    article = DebatesActivity.DebatesActivityIntent.getArticle(getArguments());
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
        articleDaemon,
        (debateId, level) -> ReplyDialog.createFragment(article.getArticleId(), debateId, level)
            .show(getFragmentManager(), "fragment_reply"));
    debateListView.setAdapter(adapter);
    debateListView.getItemAnimator().setChangeDuration(0);

    pullToRefreshLayout.setOnRefreshListener(this::refreshDebates);
    pullToRefreshLayout.setRefreshing(true);
    bind(articleDaemon.getSubject(VoteArticleSuccessEvent.class,
        VoteDebateSuccessEvent.class)).subscribe(event -> {
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

    bind(articleDaemon.getSubject(CreateLocalDebateEvent.class,
        CreateDebateSuccessEvent.class,
        CreateDebateFailedEvent.class)).subscribe(this::processDebateEvent);
  }

  private void processDebateEvent(ArticleEvent event) {
    if (event instanceof CreateDebateSuccessEvent) {
      refreshDebates();
    }
  }

  public void refreshDebates() {
    bind(articleDaemon.listDebates(article.getArticleId())).subscribe(adapter::refresh,
        throwable -> {
        },
        () -> pullToRefreshLayout.setRefreshing(false));
  }

}
