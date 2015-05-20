package io.kaif.mobile.view;

import javax.inject.Inject;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import io.kaif.mobile.KaifApplication;
import io.kaif.mobile.R;
import io.kaif.mobile.app.BaseFragment;
import io.kaif.mobile.view.daemon.AccountDaemon;
import io.kaif.mobile.view.daemon.NewsFeedDaemon;
import io.kaif.mobile.view.drawable.NewsFeedBadgeDrawable;
import io.kaif.mobile.view.widget.SlidingTabLayout;

public class HomeFragment extends BaseFragment {

  public static HomeFragment newInstance() {
    return new HomeFragment();
  }

  @InjectView(R.id.sliding_tabs)
  SlidingTabLayout slidingTabLayout;

  @InjectView(R.id.view_pager)
  ViewPager viewPager;

  @Inject
  AccountDaemon accountDaemon;

  @Inject
  NewsFeedDaemon newsFeedDaemon;

  private NewsFeedBadgeDrawable newsFeedBadgeDrawable;

  public HomeFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    KaifApplication.getInstance().beans().inject(this);
    setHasOptionsMenu(true);
    newsFeedBadgeDrawable = new NewsFeedBadgeDrawable(getResources());
  }

  @Override
  public void onResume() {
    super.onResume();
    bind(newsFeedDaemon.newsUnreadCount()).subscribe(newsFeedBadgeDrawable::changeCount,
        ignoreEx -> {

        });
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_home, menu);
    MenuItem newsFeedAction = menu.findItem(R.id.action_news_feed);
    newsFeedAction.setIcon(newsFeedBadgeDrawable);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_sign_out) {
      accountDaemon.signOut();
      return true;
    }
    if (id == R.id.action_news_feed) {
      startActivity(new NewsFeedActivity.NewsFeedActivityIntent(getActivity()));
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_home, container, false);
    ButterKnife.inject(this, view);

    viewPager.setAdapter(new HomePagerAdapter(getActivity(), getFragmentManager()));
    viewPager.setOffscreenPageLimit(2);
    slidingTabLayout.setBackgroundColor(getResources().getColor(R.color.kaif_blue));
    slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.indicator_gray));
    slidingTabLayout.setDividerColors(getResources().getColor(android.R.color.transparent));
    slidingTabLayout.setViewPager(viewPager);

    return view;
  }

}
