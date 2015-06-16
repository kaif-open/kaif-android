package io.kaif.mobile.view;

import java.util.List;

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
import io.kaif.mobile.event.vote.VoteArticleSuccessEvent;
import io.kaif.mobile.view.daemon.ArticleDaemon;
import io.kaif.mobile.view.daemon.VoteDaemon;
import io.kaif.mobile.view.viewmodel.ArticleViewModel;
import io.kaif.mobile.view.widget.OnScrollToLastListener;
import rx.Observable;

public class ArticlesFragment extends BaseFragment {

  public static final int RELOAD_EXPIRE_INTERVAL = 5 * 60 * 1000;

  @InjectView(R.id.article_list)
  RecyclerView articleListView;

  @InjectView(R.id.pull_to_refresh)
  SwipeRefreshLayout pullToRefreshLayout;

  @Inject
  ArticleDaemon articleDaemon;

  @Inject
  VoteDaemon voteDaemon;

  private long leaveTime = 0;

  private final static String ARGUMENT_IS_HOT = "IS_HOT";

  public static ArticlesFragment newInstance(boolean isHot) {
    ArticlesFragment fragment = new ArticlesFragment();
    Bundle args = new Bundle();
    args.putBoolean(ARGUMENT_IS_HOT, isHot);
    fragment.setArguments(args);
    return fragment;
  }

  private ArticleListAdapter adapter;
  private boolean isHot;

  public ArticlesFragment() {
    // Required empty public constructor
  }

  private Observable<List<ArticleViewModel>> listArticles(String startArticleId) {
    if (isHot) {
      return articleDaemon.listHotArticles(startArticleId);
    }
    return articleDaemon.listLatestArticles(startArticleId);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    KaifApplication.getInstance().beans().inject(this);
    isHot = getArguments().getBoolean(ARGUMENT_IS_HOT);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_articles, container, false);
    ButterKnife.inject(this, view);
    setupView();
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    reloadIfRequired();
  }

  @Override
  public void onPause() {
    leaveTime = System.currentTimeMillis();
    super.onPause();
  }

  private void reloadIfRequired() {
    if (System.currentTimeMillis() - leaveTime <= RELOAD_EXPIRE_INTERVAL) {
      return;
    }
    reload();
  }

  private void setupView() {
    final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    articleListView.setLayoutManager(linearLayoutManager);
    adapter = new ArticleListAdapter(voteDaemon,
        item -> startActivity(DebatesActivity.DebatesActivityIntent.create(getActivity(), item)));
    articleListView.setAdapter(adapter);
    articleListView.getItemAnimator().setChangeDuration(120);
    articleListView.addOnScrollListener(new OnScrollToLastListener() {
      private boolean loadingNextPage = false;

      @Override
      public void onScrollToLast() {
        if (loadingNextPage) {
          return;
        }
        loadingNextPage = true;
        bind(listArticles(adapter.getLastArticleId())).subscribe(adapter::addAll, throwable -> {
        }, () -> loadingNextPage = false);
      }
    });
    pullToRefreshLayout.setOnRefreshListener(this::reload);
    bind(voteDaemon.getSubject(VoteArticleSuccessEvent.class)).subscribe(event -> {
      adapter.updateVote(event.getArticleId(), event.getVoteState());
    });
  }

  private void reload() {
    pullToRefreshLayout.setRefreshing(true);
    bind(listArticles(null)).subscribe(adapter::refresh,
        throwable -> pullToRefreshLayout.setRefreshing(false),
        () -> pullToRefreshLayout.setRefreshing(false));
  }

}
