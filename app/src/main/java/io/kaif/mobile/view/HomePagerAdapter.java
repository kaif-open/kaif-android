package io.kaif.mobile.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import io.kaif.mobile.R;

public class HomePagerAdapter extends FragmentPagerAdapter {

  private Context context;

  public HomePagerAdapter(Context context, FragmentManager fm) {
    super(fm);
    this.context = context;
  }

  @Override
  public int getCount() {
    return 3;
  }

  @Override
  public Fragment getItem(int position) {
    if (position == 0) {
      return ArticlesFragment.newInstance(true);
    } else if (position == 1) {
      return ArticlesFragment.newInstance(false);
    } else {
      return LatestDebatesFragment.newInstance();
    }
  }

  @Override
  public CharSequence getPageTitle(int position) {
    switch (position) {
      case 0:
        return context.getString(R.string.hot);
      case 1:
        return context.getString(R.string.latest);
      case 2:
        return context.getString(R.string.debate);
      default:
        return null;
    }
  }
}
