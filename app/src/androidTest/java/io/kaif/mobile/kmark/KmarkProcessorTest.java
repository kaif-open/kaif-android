package io.kaif.mobile.kmark;

import android.test.AndroidTestCase;
import android.text.SpannableStringBuilder;
import android.text.style.QuoteSpan;
import android.text.style.StyleSpan;

public class KmarkProcessorTest extends AndroidTestCase {

  public void testNestSpan_order_as_begin() {
    SpannableStringBuilder result = (SpannableStringBuilder) KmarkProcessor.process(getContext(),
        "> *Sample* text");
    Object[] spans = result.getSpans(0, result.length(), Object.class);
    assertEquals(QuoteSpan.class, spans[0].getClass());
    assertEquals(StyleSpan.class, spans[1].getClass());
  }

}