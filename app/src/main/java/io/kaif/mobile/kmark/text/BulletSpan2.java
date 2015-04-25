package io.kaif.mobile.kmark.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;

public class BulletSpan2 implements LeadingMarginSpan {
  private int leading;
  private final int gapWidth;
  private final int bulletRadius;

  private static Path sBulletPath = null;

  public BulletSpan2(int leading, int gapWidth, int bulletRadius) {
    this.leading = leading;
    this.gapWidth = gapWidth;
    this.bulletRadius = bulletRadius;
  }

  public int getLeadingMargin(boolean first) {
    return leading + (2 * bulletRadius + gapWidth);
  }

  public void drawLeadingMargin(Canvas c,
      Paint p,
      int x,
      int dir,
      int top,
      int baseline,
      int bottom,
      CharSequence text,
      int start,
      int end,
      boolean first,
      Layout l) {
    if (((Spanned) text).getSpanStart(this) == start) {
      Paint.Style style = p.getStyle();
      p.setStyle(Paint.Style.FILL);

      if (c.isHardwareAccelerated()) {
        if (sBulletPath == null) {
          sBulletPath = new Path();
          // Bullet is slightly better to avoid aliasing artifacts on mdpi devices.
          sBulletPath.addCircle(0.0f, 0.0f, 1.2f * bulletRadius, Path.Direction.CW);
        }

        c.save();
        c.translate(x + dir * bulletRadius + leading, (top + bottom) / 2.0f);
        c.drawPath(sBulletPath, p);
        c.restore();
      } else {
        c.drawCircle(x + dir * bulletRadius + leading, (top + bottom) / 2.0f, bulletRadius, p);
      }

      p.setStyle(style);
    }
  }
}
