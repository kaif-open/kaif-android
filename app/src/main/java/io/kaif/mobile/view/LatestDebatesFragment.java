package io.kaif.mobile.view;

import java.util.List;

import javax.inject.Inject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

  public static final int RELOAD_EXPIRE_INTERVAL = 5 * 60 * 1000;

  @InjectView(R.id.debate_list)
  RecyclerView debateListView;

  @InjectView(R.id.pull_to_refresh)
  SwipeRefreshLayout pullToRefreshLayout;

  @Inject
  ArticleDaemon articleDaemon;

  private long leaveTime = 0;

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
    GestureDetectorCompat gestureDetector = new GestureDetectorCompat(getActivity(),
        new OnItemClickListener());
    debateListView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
      @Override
      public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return gestureDetector.onTouchEvent(e);
      }

      @Override
      public void onTouchEvent(RecyclerView rv, MotionEvent e) {

      }
    });
    pullToRefreshLayout.setOnRefreshListener(LatestDebatesFragment.this::reload);
  }

  private void reload() {
    pullToRefreshLayout.setRefreshing(true);
    bind(listDebates(null)).subscribe(adapter::refresh, throwable -> {
    }, () -> pullToRefreshLayout.setRefreshing(false));
  }

  private Observable<List<DebateViewModel>> listDebates(String startDebateId) {
    return articleDaemon.listLatestDebates(startDebateId);
  }

  private class OnItemClickListener extends GestureDetector.SimpleOnGestureListener {
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
      View view = debateListView.findChildViewUnder(e.getX(), e.getY());
      if (view == null) {
        return false;
      }
      int position = debateListView.getChildAdapterPosition(view);
      DebateViewModel debateViewModel = adapter.findItem(position);
      if (debateViewModel == null) {
        return false;
      }
      Intent intent = DebatesActivity.DebatesActivityIntent.create(getActivity(), debateViewModel);
      startActivity(intent);
      return true;
    }
  }
}