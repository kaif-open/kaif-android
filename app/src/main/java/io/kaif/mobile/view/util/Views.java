package io.kaif.mobile.view.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created on 15/04/10.
 */
public class Views {
  public static float convertDpToPixel(float dp, Context context) {
    Resources resources = context.getResources();
    DisplayMetrics metrics = resources.getDisplayMetrics();
    return dp * (metrics.densityDpi / 160f);
  }
}
