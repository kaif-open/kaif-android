/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * Copyright (C) 2015 Koji Lin <koji.lin@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.kaif.mobile.kmark;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.QuoteSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import io.kaif.mobile.kmark.text.BulletSpan2;
import io.kaif.mobile.kmark.text.CodeBlockSpan;
import io.kaif.mobile.kmark.text.SuperscriptSpan2;

/**
 * Decorator for android spanned
 *
 * @author koji lin <koji.lin@gmail.com>
 */
public class DefaultDecorator implements Decorator {

  private int leading;

  private int bulletGap;
  private int bulletRadius;
  private int codeBackgroundColor;

  public DefaultDecorator(int leading, int bulletGap, int bulletRadius, int codeBackgroundColor) {
    this.leading = leading;
    this.bulletGap = bulletGap;
    this.bulletRadius = bulletRadius;
    this.codeBackgroundColor = codeBackgroundColor;
  }

  @Override
  public void openParagraph(SpannableStringBuilder out) {
    appendParagraphNewLines(out);
  }

  private void appendParagraphNewLines(SpannableStringBuilder out) {
    int len = out.length();
    if (len >= 1 && out.charAt(len - 1) == '\n') {
      if (len >= 2 && out.charAt(len - 2) == '\n') {
        return;
      }

      out.append("\n");
      return;
    }

    if (out.length() != 0) {
      out.append("\n\n");
    }
  }

  @Override
  public void closeParagraph(SpannableStringBuilder out) {

  }

  @Override
  public void openCodeBlock(SpannableStringBuilder out) {
    start(out, new CodeBlock());
  }

  @Override
  public void closeCodeBlock(SpannableStringBuilder out) {
    end(out, CodeBlock.class, new CodeBlockSpan(codeBackgroundColor));
  }

  @Override
  public void openCodeSpan(SpannableStringBuilder out) {
    start(out, new Code());
  }

  @Override
  public void closeCodeSpan(SpannableStringBuilder out) {
    end(out, Code.class, new BackgroundColorSpan(codeBackgroundColor));
  }

  @Override
  public void openStrong(SpannableStringBuilder out) {
    start(out, new Bold());
  }

  @Override
  public void closeStrong(SpannableStringBuilder out) {
    end(out, Bold.class, new StyleSpan(Typeface.BOLD));
  }

  @Override
  public void openStrike(SpannableStringBuilder out) {
    start(out, new Strike());
  }

  @Override
  public void closeStrike(SpannableStringBuilder out) {
    end(out, Strike.class, new StrikethroughSpan());
  }

  @Override
  public void openEmphasis(SpannableStringBuilder out) {
    start(out, new Italic());
  }

  @Override
  public void closeEmphasis(SpannableStringBuilder out) {
    end(out, Italic.class, new StyleSpan(Typeface.ITALIC));
  }

  @Override
  public void openSuper(SpannableStringBuilder out) {
    start(out, new Super());
  }

  @Override
  public void closeSuper(SpannableStringBuilder out) {
    end(out, Super.class, new SuperscriptSpan2());
  }

  @Override
  public void openOrderedList(SpannableStringBuilder out) {
    appendParagraphNewLines(out);
    start(out, new OrderedList());
  }

  @Override
  public void closeOrderedList(SpannableStringBuilder out) {
    out.removeSpan(getLast(out, OrderedList.class));
  }

  @Override
  public void openUnorderedList(SpannableStringBuilder out) {
    appendParagraphNewLines(out);
  }

  @Override
  public void closeUnorderedList(SpannableStringBuilder out) {
  }

  @Override
  public void openOrderedListItem(SpannableStringBuilder out) {
    appendParagraphNewLines(out);
    start(out, new OrderedListItem());
  }

  @Override
  public void closeOrderedListItem(SpannableStringBuilder out) {
    OrderedList orderedList = getLast(out, OrderedList.class);
    if (orderedList != null) {
      int number = orderedList.getAndIncrement();
      int where = out.getSpanStart(getLast(out, OrderedListItem.class));
      out.insert(where, Integer.toString(number) + ". ");
    }
    //check BulletSpan2
    end(out, OrderedListItem.class, new LeadingMarginSpan.LeadingMarginSpan2.Standard(leading));
  }

  @Override
  public void openUnOrderedListItem(SpannableStringBuilder out) {
    appendParagraphNewLines(out);
    start(out, new UnOrderedListItem());
  }

  @Override
  public void closeUnOrderedListItem(SpannableStringBuilder out) {
    end(out, UnOrderedListItem.class, new BulletSpan2(leading, bulletGap, bulletRadius));
  }

  @Override
  public void openLink(SpannableStringBuilder out) {
    start(out, new Link());
  }

  @Override
  public void closeLink(SpannableStringBuilder out, String url) {
    end(out, Link.class, new URLSpan(url));
  }

  @Override
  public void openBlockquote(SpannableStringBuilder out) {
    start(out, new Blockquote());
  }

  @Override
  public void closeBlockquote(SpannableStringBuilder out) {
    end(out, Blockquote.class, new QuoteSpan());
  }

  private static <T> T getLast(Spanned text, Class<T> kind) {
    T[] objs = text.getSpans(0, text.length(), kind);
    if (objs.length == 0) {
      return null;
    } else {
      return objs[objs.length - 1];
    }
  }

  private static void start(SpannableStringBuilder text, Object mark) {
    int len = text.length();
    text.setSpan(mark, len, len, Spannable.SPAN_MARK_MARK);
  }

  private static <T> void end(SpannableStringBuilder text, Class<T> kind, Object repl) {
    int len = text.length();
    T obj = getLast(text, kind);
    int where = text.getSpanStart(obj);
    text.removeSpan(obj);

    if (where != len) {
      text.setSpan(repl, where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
  }

  private static class Bold {
  }

  private static class Strike {

  }

  private static class Italic {
  }

  private static class Blockquote {
  }

  private static class Super {
  }

  private static class Code {
  }

  private static class Link {
  }

  private static class UnOrderedListItem {
  }

  private static class OrderedListItem {
  }

  private static class CodeBlock {
  }

  private static class OrderedList {
    private int index;

    OrderedList() {
      this.index = 1;
    }

    int getAndIncrement() {
      int current = index;
      index += 1;
      return current;
    }

  }

}
