package io.kaif.mobile.view.widget;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class OnScrollToLastListener extends RecyclerView.OnScrollListener {

  @Override
  public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

    int visibleItemCount = layoutManager.getChildCount();
    int totalItemCount = layoutManager.getItemCount();
    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
      onScrollToLast();
    }
  }

  public abstract void onScrollToLast();
}
