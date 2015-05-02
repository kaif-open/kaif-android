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
import io.kaif.mobile.view.daemon.ArticleDaemon;
import io.kaif.mobile.view.viewmodel.DebateViewModel;
import io.kaif.mobile.view.widget.OnScrollToLastListener;
import rx.Observable;

public class LatestDebatesFragment extends BaseFragment {

  @InjectView(R.id.debate_list)
  RecyclerView debateListView;

  @InjectView(R.id.pull_to_refresh)
  SwipeRefreshLayout pullToRefreshLayout;

  @Inject
  ArticleDaemon articleDaemon;

  public static LatestDebatesFragment newInstance() {
    LatestDebatesFragment fragment = new LatestDebatesFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  private LatestDebateListAdapter adapter;

  public LatestDebatesFragment() {
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
    final View view = inflater.inflate(R.layout.fragment_latest_debates, container, false);
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
    debateListView.setLayoutManager(linearLayoutManager);
    adapter = new LatestDebateListAdapter();
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
        bind(listDebates(adapter.getLastDebateId())).subscribe(adapter::addAll, throwable -> {
        }, () -> loadingNextPage = false);
      }
    });
    pullToRefreshLayout.setOnRefreshListener(this::loadFirstPage);
  }

  private void loadFirstPage() {
    bind(listDebates(null)).subscribe(adapter::refresh, throwable -> {
    }, () -> pullToRefreshLayout.setRefreshing(false));
  }

  private Observable<List<DebateViewModel>> listDebates(String startDebateId) {
    return articleDaemon.listLatestDebates(startDebateId);
  }
}