package io.kaif.mobile.view.graphics.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class Triangle extends Drawable {

  private Paint paint;
  private int alpha;
  Path triangle;
  private boolean reverse;

  public Triangle(int color) {
    this(color, false);
  }

  public Triangle(int color, boolean reverse) {
    this.reverse = reverse;
    this.triangle = new Path();

    this.paint = new Paint();
    this.paint.setAntiAlias(true);
    this.paint.setColor(color);
    this.paint.setStyle(Paint.Style.FILL);
  }

  @Override
  public int getOpacity() {
    return PixelFormat.OPAQUE;
  }

  @Override
  public void draw(Canvas canvas) {
    canvas.drawPath(triangle, this.paint);
  }

  @Override
  protected void onBoundsChange(Rect bounds) {
    triangle.reset();
    if (reverse) {
      triangle.moveTo(bounds.left + bounds.width() / 2f, bounds.bottom);
      triangle.lineTo(bounds.left + bounds.width(), bounds.top);
      triangle.lineTo(bounds.left, bounds.top);
      return;
    }
    triangle.moveTo(bounds.left + bounds.width() / 2f, bounds.top);
    triangle.lineTo(bounds.left + bounds.width(), bounds.bottom);
    triangle.lineTo(bounds.left, bounds.bottom);
  }

  @Override
  public void setAlpha(int alpha) {
    this.alpha = alpha;
  }

  @Override
  public int getAlpha() {
    return alpha;
  }

  @Override
  public void setColorFilter(ColorFilter cf) {
    paint.setColorFilter(cf);
  }
}
