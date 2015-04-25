package io.kaif.mobile.kmark.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

public class CodeBlockSpan implements LineBackgroundSpan {
  private int color;

  public CodeBlockSpan(int color) {
    this.color = color;
  }

  @Override
  public void drawBackground(Canvas c,
      Paint p,
      int left,
      int right,
      int top,
      int baseline,
      int bottom,
      CharSequence text,
      int start,
      int end,
      int lnum) {
    int oldcolor = p.getColor();
    p.setColor(color);
    c.drawRect(left, top, right, bottom, p);
    p.setColor(oldcolor);
  }
}
