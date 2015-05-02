package io.kaif.mobile.view;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import io.kaif.mobile.KaifApplication;
import io.kaif.mobile.R;
import io.kaif.mobile.app.BaseFragment;
import io.kaif.mobile.view.widget.SlidingTabLayout;

public class HomeFragment extends BaseFragment {

  public static HomeFragment newInstance() {
    return new HomeFragment();
  }

  @InjectView(R.id.sliding_tabs)
  SlidingTabLayout slidingTabLayout;

  @InjectView(R.id.view_pager)
  ViewPager viewPager;

  public HomeFragment() {

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
