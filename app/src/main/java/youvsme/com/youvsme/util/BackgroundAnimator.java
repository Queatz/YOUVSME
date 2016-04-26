package youvsme.com.youvsme.util;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;

/**
 * Created by jacob on 4/21/16.
 */
public abstract class BackgroundAnimator {
    public BackgroundAnimator(final View view, int toColor) {
        int colorFrom = Color.TRANSPARENT;
        int colorTo = view.getContext().getResources().getColor(toColor);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo, colorFrom);
        colorAnimation.setDuration(250);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                onComplete();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        colorAnimation.start();
    }

    public abstract void onComplete();
}
