package io.kaif.mobile.view;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.kaif.mobile.KaifApplication;
import io.kaif.mobile.R;
import io.kaif.mobile.app.BaseFragment;
import io.kaif.mobile.view.daemon.AccountDaemon;
import io.kaif.mobile.view.daemon.FeedDaemon;
import io.kaif.mobile.view.drawable.NewsFeedBadgeDrawable;

public class HomeFragment extends BaseFragment {

  public static HomeFragment newInstance() {
    return new HomeFragment();
  }

  @BindView(R.id.sliding_tabs)
  TabLayout slidingTabLayout;

  @BindView(R.id.view_pager)
  ViewPager viewPager;

  @Inject
  AccountDaemon accountDaemon;

  @Inject
  FeedDaemon feedDaemon;

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
    bind(feedDaemon.newsUnreadCount()).subscribe(newsFeedBadgeDrawable::changeCount, ignoreEx -> {

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
    if (id == R.id.action_honor) {
      startActivity(new HonorActivityIntent(getActivity()));
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
    ButterKnife.bind(this, view);

    viewPager.setAdapter(new HomePagerAdapter(getActivity(), getFragmentManager()));
    viewPager.setOffscreenPageLimit(2);
    slidingTabLayout.setBackgroundColor(getResources().getColor(R.color.kaif_blue));
    slidingTabLayout.setupWithViewPager(viewPager);
    viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(slidingTabLayout));

    return view;
  }

}
