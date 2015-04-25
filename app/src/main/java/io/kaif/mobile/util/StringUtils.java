package io.kaif.mobile.util;

public class StringUtils {

  /**
   * copy from TextUtils
   */
  public static String join(CharSequence delimiter, Object[] tokens) {
    StringBuilder sb = new StringBuilder();
    boolean firstTime = true;
    for (Object token : tokens) {
      if (firstTime) {
        firstTime = false;
      } else {
        sb.append(delimiter);
      }
      sb.append(token);
    }
    return sb.toString();
  }

}
