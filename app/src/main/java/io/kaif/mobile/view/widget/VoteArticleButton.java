package io.kaif.mobile.view.widget;

import android.animation.Animator;
import android.content.Context;
import android.graphics.drawable.InsetDrawable;
import android.util.AttributeSet;
import android.widget.Button;
import io.kaif.mobile.R;
import io.kaif.mobile.model.Vote;
import io.kaif.mobile.view.animation.VoteAnimation;
import io.kaif.mobile.view.graphics.drawable.Triangle;
import io.kaif.mobile.view.util.Views;

public class VoteArticleButton extends Button {

  private Vote.VoteState voteState;

  private OnVoteClickListener onVoteClickListener;

  public VoteArticleButton(Context context) {
    this(context, null);
  }

  public VoteArticleButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public VoteArticleButton(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context context) {
    setBackground(new InsetDrawable(new Triangle(context.getResources()
        .getColor(R.color.vote_state_empty)),
        (int) Views.convertDpToPixel(12, context),
        (int) Views.convertDpToPixel(4, context),
        (int) Views.convertDpToPixel(12, context),
        (int) Views.convertDpToPixel(4, context)));
    voteState = Vote.VoteState.EMPTY;
    setOnClickListener(v -> {
      if (onVoteClickListener != null) {
        Vote.VoteState from = this.voteState;
        Vote.VoteState to = (this.voteState == Vote.VoteState.EMPTY
                             ? Vote.VoteState.UP
                             : Vote.VoteState.EMPTY);
        onVoteClickListener.onVoteClicked(from, to);
      }
    });
  }

  public void setOnVoteClickListener(OnVoteClickListener onVoteClickListener) {
    this.onVoteClickListener = onVoteClickListener;
  }

  public void updateVoteState(Vote.VoteState voteState) {
    this.voteState = voteState;
    showVoteColor(false);
  }

  public void showVoteColor(boolean showAnimation) {
    final Animator animator;
    switch (voteState) {
      case UP: {
        animator = VoteAnimation.voteUpAnimation(this);
        break;
      }
      default:
        animator = VoteAnimation.voteUpReverseAnimation(this);
        break;
    }
    if (!showAnimation) {
      animator.setDuration(0);
    }
    animator.start();
  }
}
