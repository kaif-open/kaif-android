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
import io.kaif.mobile.event.article.VoteArticleSuccessEvent;
import io.kaif.mobile.view.daemon.ArticleDaemon;

public class HotArticlesFragment extends BaseFragment {

  @InjectView(R.id.article_list)
  RecyclerView articleListView;

  @InjectView(R.id.pull_to_refresh)
  SwipeRefreshLayout pullToRefreshLayout;

  @Inject
  ArticleDaemon articleDaemon;

  private boolean loadingNextPage = false;

  public static HotArticlesFragment newInstance() {
    HotArticlesFragment fragment = new HotArticlesFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  private ArticleListAdapter adapter;

  public HotArticlesFragment() {
    // Required empty public constructor
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
    final View view = inflater.inflate(R.layout.fragment_hot_articles, container, false);
    ButterKnife.inject(this, view);
    setupView();
    fillContent();
    return view;
  }

  private void fillContent() {
    loadFirstPage();
  }

  private void setupView() {
    final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    articleListView.setLayoutManager(linearLayoutManager);

    adapter = new ArticleListAdapter(articleDaemon,
        item -> startActivity(DebatesActivity.DebatesActivityIntent.create(getActivity(), item)));
    articleListView.setAdapter(adapter);
    articleListView.getItemAnimator().setChangeDuration(120);
    articleListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (loadingNextPage) {
          return;
        }
        int visibleItemCount = linearLayoutManager.getChildCount();
        int totalItemCount = linearLayoutManager.getItemCount();
        int pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();

        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
          loadingNextPage = true;
          bind(articleDaemon.listHotArticles(adapter.getLastArticleId())).subscribe(adapter::addAll,
              throwable -> {
              },
              () -> loadingNextPage = false);
        }
      }
    });
    pullToRefreshLayout.setOnRefreshListener(this::loadFirstPage);
    bind(articleDaemon.getSubject(VoteArticleSuccessEvent.class)).subscribe(event -> {
      adapter.updateVote(event.getArticleId(), event.getVoteState());
    });
  }

  private void loadFirstPage() {
    bind(articleDaemon.listHotArticles(null)).subscribe(adapter::refresh, throwable -> {
    }, () -> pullToRefreshLayout.setRefreshing(false));
  }
}
