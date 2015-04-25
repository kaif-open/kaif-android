package io.kaif.mobile.view.widget;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.InsetDrawable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import io.kaif.mobile.R;
import io.kaif.mobile.model.Vote;
import io.kaif.mobile.view.animation.VoteAnimation;
import io.kaif.mobile.view.graphics.drawable.Triangle;
import io.kaif.mobile.view.util.Views;

public class DebateActions extends LinearLayout {

  public static final int BUTTON_SIZE_DP = 40;

  public static final int BUTTON_PADDING_DP = 12;

  private Vote.VoteState voteState;

  private OnVoteClickListener onVoteClickListener;

  private Button downVote;

  private Button upVote;

  private ImageButton reply;

  public DebateActions(Context context) {
    this(context, null);
  }

  public DebateActions(Context context, AttributeSet attrs) {
    super(context, attrs);
    setOrientation(HORIZONTAL);
    initViews(context);
  }

  public DebateActions(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setOrientation(HORIZONTAL);
    initViews(context);
  }

  private void initViews(Context context) {
    upVote = new Button(context);
    int px = (int) Views.convertDpToPixel(BUTTON_SIZE_DP, context);
    int paddingPx = (int) Views.convertDpToPixel(BUTTON_PADDING_DP, context);

    addView(new Space(context), new LinearLayout.LayoutParams(0, 0, 1));

    upVote.setBackground(new InsetDrawable(new Triangle(context.getResources()
        .getColor(R.color.vote_state_empty)), paddingPx));
    addView(upVote, new LinearLayout.LayoutParams(px, px));
    upVote.setOnClickListener(v -> {
      if (onVoteClickListener != null) {
        Vote.VoteState from = voteState;
        Vote.VoteState to = (voteState == Vote.VoteState.UP
                             ? Vote.VoteState.EMPTY
                             : Vote.VoteState.UP);
        onVoteClickListener.onVoteClicked(from, to);
      }
    });

    addView(new Space(context), new LinearLayout.LayoutParams(0, 0, 1));

    downVote = new Button(context);
    downVote.setBackground(new InsetDrawable(new Triangle(context.getResources()
        .getColor(R.color.vote_state_empty), true), paddingPx));
    addView(downVote, new LinearLayout.LayoutParams(px, px));
    downVote.setOnClickListener(v -> {
      if (onVoteClickListener != null) {
        Vote.VoteState from = voteState;
        Vote.VoteState to = (voteState == Vote.VoteState.DOWN
                             ? Vote.VoteState.EMPTY
                             : Vote.VoteState.DOWN);
        onVoteClickListener.onVoteClicked(from, to);
      }
    });

    addView(new Space(context), new LinearLayout.LayoutParams(0, 0, 1));

    reply = new ImageButton(context);
    reply.setImageResource(R.drawable.ic_reply);
    reply.setScaleType(ImageView.ScaleType.CENTER);
    reply.setBackground(null);
    addView(reply, new LinearLayout.LayoutParams(px, px));
    addView(new Space(context), new LinearLayout.LayoutParams(0, 0, 1));
  }

  public void setOnReplyClickListener(OnClickListener onClickListener) {
    reply.setOnClickListener(view -> {
      Animation animation = AnimationUtils.loadAnimation(view.getContext(),
          R.anim.scale_action_icon);
      view.startAnimation(animation);
      onClickListener.onClick(view);
    });
  }

  public void setOnVoteClickListener(OnVoteClickListener onVoteClickListener) {
    this.onVoteClickListener = onVoteClickListener;
  }

  public void updateVoteState(Vote.VoteState voteState) {
    this.voteState = voteState;
    int upColor = R.color.vote_state_empty;
    int downColor = R.color.vote_state_empty;

    switch (voteState) {
      case UP:
        upColor = R.color.vote_state_up;
        break;
      case DOWN:
        downColor = R.color.vote_state_down;
        break;
    }
    downVote.getBackground()
        .setColorFilter(getContext().getResources().getColor(downColor), PorterDuff.Mode.SRC_IN);
    downVote.getBackground().invalidateSelf();
    upVote.getBackground()
        .setColorFilter(getContext().getResources().getColor(upColor), PorterDuff.Mode.SRC_IN);
    upVote.getBackground().invalidateSelf();
  }

  public void playAnimations(Vote.VoteState from) {
    playUpVoteAnimation(from, voteState);
    playDownVoteAnimation(from, voteState);
  }

  private void playUpVoteAnimation(Vote.VoteState prevState, Vote.VoteState newVoteState) {
    switch (prevState) {
      case DOWN:
      case EMPTY:
        switch (newVoteState) {
          case UP:
            VoteAnimation.voteUpAnimation(upVote).start();
            break;
        }
        break;
      case UP:
        switch (newVoteState) {
          case DOWN:
          case EMPTY:
            VoteAnimation.voteUpReverseAnimation(upVote).start();
            break;
        }
        break;
    }
  }

  private void playDownVoteAnimation(Vote.VoteState prevState, Vote.VoteState newVoteState) {
    switch (prevState) {
      case DOWN:
        switch (newVoteState) {
          case UP:
          case EMPTY:
            VoteAnimation.voteDownReverseAnimation(downVote).start();
            break;
        }
        break;
      case EMPTY:
      case UP:
        switch (newVoteState) {
          case DOWN:
            VoteAnimation.voteDownAnimation(downVote).start();
            break;
        }
        break;
    }
  }
}
