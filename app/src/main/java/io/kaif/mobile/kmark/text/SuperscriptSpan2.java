package io.kaif.mobile.kmark.text;

import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.SuperscriptSpan;

public class SuperscriptSpan2 extends SuperscriptSpan {
  @Override
  public void updateDrawState(@NonNull TextPaint tp) {
    tp.setTextSize(tp.getTextSize() * 0.75f);
    tp.baselineShift += (int) (tp.ascent() / 2);
  }

  @Override
  public void updateMeasureState(@NonNull TextPaint tp) {
    tp.setTextSize(tp.getTextSize() * 0.75f);
    tp.baselineShift += (int) (tp.ascent() / 2);
  }
}
