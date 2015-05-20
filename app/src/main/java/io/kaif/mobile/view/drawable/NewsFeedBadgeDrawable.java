package io.kaif.mobile.view.drawable;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import io.kaif.mobile.R;
import io.kaif.mobile.view.util.Views;

public class NewsFeedBadgeDrawable extends Drawable {

  private long count;
  private TextPaint textPaint;
  private Paint circlePaint;
  private final Resources resources;
  private final Drawable icon;
  private StaticLayout textLayout;
  private int countRadius;

  public NewsFeedBadgeDrawable(Resources resources) {
    this.resources = resources;
    icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_notifications_white, null);

    textPaint = new TextPaint();
    textPaint.setTextSize((int) (11.0f * resources.getDisplayMetrics().density + 0.5f));
    textPaint.setColor(resources.getColor(android.R.color.white));
    textPaint.setAntiAlias(true);

    circlePaint = new Paint();
    circlePaint.setColor(0xffff0000);
    circlePaint.setAntiAlias(true);

    changeCount(0);
  }

  @Override
  public void draw(Canvas canvas) {
    icon.draw(canvas);
    if (count == 0) {
      return;
    }
    canvas.save();
    canvas.translate(Views.convertDpToPixel(24, resources), Views.convertDpToPixel(8, resources));
    canvas.drawCircle(0, 0, countRadius, circlePaint);
    canvas.translate(-textLayout.getWidth() / 2 - 1, -textLayout.getHeight() / 2 - 1);
    textLayout.draw(canvas);
    canvas.restore();

  }

  @Override
  public void setAlpha(int alpha) {
    icon.setAlpha(alpha);
    textPaint.setAlpha(alpha);
    circlePaint.setAlpha(alpha);
  }

  @Override
  public void setColorFilter(ColorFilter cf) {
    icon.setColorFilter(cf);
    textPaint.setColorFilter(cf);
    circlePaint.setColorFilter(cf);
  }

  @Override
  public int getIntrinsicHeight() {
    return (int) Views.convertDpToPixel(36, resources);
  }

  @Override
  public int getIntrinsicWidth() {
    return (int) Views.convertDpToPixel(36, resources);
  }

  @Override
  public int getOpacity() {
    return PixelFormat.TRANSLUCENT;
  }

  private String count() {
    return count > 10 ? "10+" : String.valueOf(count);
  }

  public final void changeCount(int count) {
    this.count = count;
    int textWidth = (int) (textPaint.measureText(count()));
    textLayout = new StaticLayout(count(),
        textPaint,
        textWidth,
        Layout.Alignment.ALIGN_CENTER,
        1.0f,
        0.0f,
        false);
    countRadius = (int) (Math.sqrt(Math.pow(textLayout.getWidth() / 2.0, 2)
        + Math.pow(textLayout.getHeight() / 2.0, 2)));
  }

  @Override
  protected void onBoundsChange(Rect bounds) {
    int size = (int) Views.convertDpToPixel(24, resources);
    int horizontalPadding = Math.max(0, (bounds.width() - size) / 2);
    int verticalPadding = Math.max(0, (bounds.height() - size) / 2);

    icon.setBounds(horizontalPadding,
        verticalPadding,
        Math.min(horizontalPadding + size, bounds.right),
        Math.min(verticalPadding + size, bounds.bottom));
  }
}
