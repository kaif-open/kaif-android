package io.kaif.mobile.view;

import java.util.List;

import javax.inject.Inject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import io.kaif.mobile.KaifApplication;
import io.kaif.mobile.R;
import io.kaif.mobile.app.BaseFragment;
import io.kaif.mobile.view.daemon.FeedDaemon;
import io.kaif.mobile.view.viewmodel.FeedAssetViewModel;
import io.kaif.mobile.view.widget.OnScrollToLastListener;
import rx.Observable;

public class NewsFeedActivityFragment extends BaseFragment {

  @InjectView(R.id.debate_list)
  RecyclerView debateListView;

  @InjectView(R.id.pull_to_refresh)
  SwipeRefreshLayout pullToRefreshLayout;

  @Inject
  FeedDaemon feedDaemon;

  private NewsFeedListAdapter adapter;

  public NewsFeedActivityFragment() {
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
    View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
    ButterKnife.inject(this, view);
    setupView();
    fillContent();
    return view;
  }

  private void fillContent() {
    reload();
  }

  private void setupView() {
    final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    debateListView.setLayoutManager(linearLayoutManager);
    adapter = new NewsFeedListAdapter();
    adapter.setOnItemClickListener(debateViewModel -> {
      Intent intent = DebatesActivity.DebatesActivityIntent.create(getActivity(), debateViewModel);
      startActivity(intent);
    });

    debateListView.setAdapter(adapter);
    debateListView.getItemAnimator().setChangeDuration(120);
    debateListView.addOnScrollListener(new OnScrollToLastListener() {
      private boolean loadingNextPage = false;

      @Override
      public void onScrollToLast() {
        if (loadingNextPage) {
          return;
        }
        loadingNextPage = true;
        bind(listFeedAssets(adapter.getLastAssetId())).subscribe(adapter::addAll, throwable -> {
        }, () -> loadingNextPage = false);
      }
    });
    pullToRefreshLayout.setOnRefreshListener(this::reload);
  }

  private void reload() {
    pullToRefreshLayout.setRefreshing(true);
    bind(listFeedAssets(null)).subscribe(adapter::refresh, throwable -> {
    }, () -> pullToRefreshLayout.setRefreshing(false));
  }

  private Observable<List<FeedAssetViewModel>> listFeedAssets(String feedAssetId) {
    if (TextUtils.isEmpty(feedAssetId)) {
      return feedDaemon.listAndAcknowledgeIfRequired();
    }
    return feedDaemon.listNewsFeed(feedAssetId);
  }
}
