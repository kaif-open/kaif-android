package io.kaif.mobile.view.widget;

import android.text.Layout;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * fix url span can't click problem
 */
public class ClickableSpanTouchListener implements View.OnTouchListener {

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    Spanned spanned = (Spanned) ((TextView) v).getText();
    TextView widget = (TextView) v;
    int action = event.getAction();

    if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
      int x = (int) event.getX();
      int y = (int) event.getY();

      x -= widget.getTotalPaddingLeft();
      y -= widget.getTotalPaddingTop();

      x += widget.getScrollX();
      y += widget.getScrollY();

      Layout layout = widget.getLayout();
      int line = layout.getLineForVertical(y);
      int off = layout.getOffsetForHorizontal(line, x);

      ClickableSpan[] link = spanned.getSpans(off, off, ClickableSpan.class);

      if (link.length != 0) {
        if (action == MotionEvent.ACTION_UP) {
          link[0].onClick(widget);
        }
        return true;
      }
    }
    return false;
  }
}
