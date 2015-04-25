package io.kaif.mobile.view.widget;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import io.kaif.mobile.model.Vote;
import io.kaif.mobile.view.animation.VoteAnimation;

public class ArticleScoreTextView extends TextView {

  private Vote.VoteState voteState;

  public ArticleScoreTextView(Context context) {
    this(context, null);
  }

  public ArticleScoreTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ArticleScoreTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void update(long score, Vote.VoteState voteState) {
    this.voteState = voteState;
    setText(String.valueOf(score));
    showVoteColor(false);
  }

  public void showVoteColor(boolean showAnimation) {
    final Animator animator;
    switch (voteState) {
      case UP: {
        animator = VoteAnimation.voteUpTextColorAnimation(this);
        break;
      }
      default:
        animator = VoteAnimation.voteUpReverseTextColorAnimation(this);
        break;
    }
    if (!showAnimation) {
      animator.setDuration(0);
    }
    animator.start();
  }

}
