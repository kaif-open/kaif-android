package io.kaif.mobile.view.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.TextView;
import io.kaif.mobile.R;

public class VoteAnimation {

  private static ValueAnimator colorChangeAnimation(View view, int from, int to) {
    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), from, to);
    colorAnimation.addUpdateListener(animation -> {
      final Integer color = (Integer) animation.getAnimatedValue();
      view.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
      view.invalidate();
    });
    return colorAnimation;
  }

  public static Animator voteUpAnimation(View view) {
    Context context = view.getContext();
    ValueAnimator colorAnimation = colorChangeAnimation(view,
        context.getResources().getColor(R.color.vote_state_empty),
        context.getResources().getColor(R.color.vote_state_up));
    AnimatorSet animatorSet = new AnimatorSet();
    final ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(view, "rotation", 0, 360f);
    animatorSet.play(colorAnimation).with(rotateAnimation);
    animatorSet.setDuration(300);
    return animatorSet;
  }

  public static Animator voteDownAnimation(View view) {
    Context context = view.getContext();
    ValueAnimator colorAnimation = colorChangeAnimation(view,
        context.getResources().getColor(R.color.vote_state_empty),
        context.getResources().getColor(R.color.vote_state_down));
    AnimatorSet animatorSet = new AnimatorSet();
    final ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(view, "rotation", 0, -360f);
    animatorSet.play(colorAnimation).with(rotateAnimation);
    animatorSet.setDuration(300);
    return animatorSet;
  }

  public static Animator voteUpReverseAnimation(View view) {
    Context context = view.getContext();
    ValueAnimator colorAnimation = colorChangeAnimation(view,
        context.getResources().getColor(R.color.vote_state_up),
        context.getResources().getColor(R.color.vote_state_empty));
    AnimatorSet animatorSet = new AnimatorSet();

    final ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(view, "rotation", 360, 0f);
    animatorSet.play(colorAnimation).with(rotateAnimation);
    animatorSet.setDuration(300);
    return animatorSet;
  }

  public static Animator voteDownReverseAnimation(View view) {
    Context context = view.getContext();
    ValueAnimator colorAnimation = colorChangeAnimation(view,
        context.getResources().getColor(R.color.vote_state_down),
        context.getResources().getColor(R.color.vote_state_empty));
    AnimatorSet animatorSet = new AnimatorSet();

    final ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(view, "rotation", -360, 0f);
    animatorSet.play(colorAnimation).with(rotateAnimation);
    animatorSet.setDuration(300);
    return animatorSet;
  }

  private static ValueAnimator colorChangeAnimation(TextView view, int from, int to) {
    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), from, to);
    colorAnimation.addUpdateListener(animation -> {
      final Integer color = (Integer) animation.getAnimatedValue();
      view.setTextColor(color);
    });
    colorAnimation.setDuration(300);
    return colorAnimation;
  }

  public static ValueAnimator voteUpTextColorAnimation(TextView view) {
    Context context = view.getContext();

    return colorChangeAnimation(view,
        context.getResources().getColor(R.color.vote_state_empty),
        context.getResources().getColor(R.color.vote_state_up));
  }

  public static ValueAnimator voteUpReverseTextColorAnimation(TextView view) {
    Context context = view.getContext();

    return colorChangeAnimation(view,
        context.getResources().getColor(R.color.vote_state_up),
        context.getResources().getColor(R.color.vote_state_empty));
  }
}
