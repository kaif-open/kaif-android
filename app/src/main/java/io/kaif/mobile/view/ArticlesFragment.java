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
import io.kaif.mobile.event.article.VoteArticleSuccessEvent;
import io.kaif.mobile.view.daemon.ArticleDaemon;
import io.kaif.mobile.view.viewmodel.ArticleViewModel;
import rx.Observable;

public class ArticlesFragment extends BaseFragment {

  @InjectView(R.id.article_list)
  RecyclerView articleListView;

  @InjectView(R.id.pull_to_refresh)
  SwipeRefreshLayout pullToRefreshLayout;

  @Inject
  ArticleDaemon articleDaemon;

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

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    KaifApplication.getInstance().beans().inject(this);
    isHot = getArguments().getBoolean(ARGUMENT_IS_HOT);
  }

  private Observable<List<ArticleViewModel>> listArticles(String startArticleId) {
    if (isHot) {
      return articleDaemon.listHotArticles(startArticleId);
    }
    return articleDaemon.listLatestArticles(startArticleId);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_articles, container, false);
    ButterKnife.inject(this, view);
    setupView();
    loadFirstPage();
    return view;
  }

  private void setupView() {
    final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    articleListView.setLayoutManager(linearLayoutManager);
    adapter = new ArticleListAdapter(articleDaemon,
        item -> startActivity(DebatesActivity.DebatesActivityIntent.create(getActivity(), item)));
    articleListView.setAdapter(adapter);
    articleListView.getItemAnimator().setChangeDuration(120);
    articleListView.addOnScrollListener(new OnLoadMoreListener());
    pullToRefreshLayout.setOnRefreshListener(this::loadFirstPage);
    bind(articleDaemon.getSubject(VoteArticleSuccessEvent.class)).subscribe(event -> {
      adapter.updateVote(event.getArticleId(), event.getVoteState());
    });
  }

  private void loadFirstPage() {
    bind(listArticles(null)).subscribe(adapter::refresh, throwable -> {
    }, () -> pullToRefreshLayout.setRefreshing(false));
  }

  private class OnLoadMoreListener extends RecyclerView.OnScrollListener {

    private boolean loadingNextPage = false;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      if (loadingNextPage) {
        return;
      }
      LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

      int visibleItemCount = layoutManager.getChildCount();
      int totalItemCount = layoutManager.getItemCount();
      int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

      if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
        loadingNextPage = true;
        bind(listArticles(adapter.getLastArticleId())).subscribe(adapter::addAll, throwable -> {
        }, () -> loadingNextPage = false);
      }
    }
  }
}
