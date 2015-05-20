package io.kaif.mobile.view.graphics.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import io.kaif.mobile.R;
import io.kaif.mobile.view.util.Views;

public class LevelDrawable extends Drawable {

  public static final int MAX_NESTED_LEVEL = 7;
  private Paint paint;
  private int alpha;
  private int innerLevel = 1;
  private int paddingDp;
  private int lineWidthDp;
  private int backgroundColor;
  private final int paddingVertical;

  public LevelDrawable(Context context, int innerLevel, int backgroundColor) {
    this.backgroundColor = backgroundColor;
    this.paddingDp = (int) Views.convertDpToPixel(12, context);
    this.paddingVertical = (int) Views.convertDpToPixel(4, context);
    this.lineWidthDp = (int) Views.convertDpToPixel(2, context);
    this.innerLevel = Math.min(innerLevel, MAX_NESTED_LEVEL);
    this.paint = new Paint();
    this.paint.setAntiAlias(true);
    this.paint.setColor(context.getResources().getColor(R.color.kaif_blue_light));
    this.paint.setStyle(Paint.Style.FILL);
    this.paint.setStrokeWidth(lineWidthDp);
  }

  @Override
  public int getOpacity() {
    return PixelFormat.TRANSLUCENT;
  }

  @Override
  public void draw(Canvas canvas) {
    canvas.drawColor(backgroundColor);
    for (int i = 1; i < innerLevel; ++i) {
      final int x = paddingDp * i - lineWidthDp;
      canvas.drawLine(x, 0, x, canvas.getHeight(), paint);
    }
  }

  @Override
  public boolean getPadding(Rect padding) {
    padding.set(paddingDp * (innerLevel - 1) + lineWidthDp * 2,
        paddingVertical,
        lineWidthDp,
        paddingVertical);
    return true;
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
