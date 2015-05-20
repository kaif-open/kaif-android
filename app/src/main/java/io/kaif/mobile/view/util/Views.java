package io.kaif.mobile.view.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class Views {
  public static float convertDpToPixel(float dp, Context context) {
    return convertDpToPixel(dp, context.getResources());
  }

  public static float convertDpToPixel(float dp, Resources resources) {

    DisplayMetrics metrics = resources.getDisplayMetrics();
    return dp * (metrics.densityDpi / 160f);
  }
}
