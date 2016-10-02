package youvsme.com.youvsme;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.util.Random;

import youvsme.com.youvsme.util.Helpers;

/**
 * Created by jacob on 9/28/16.
 */
public class WiggleDreamAnimation extends Animation {
    private View view;
    private double dir;
    float startX;
    float startY;
    float speed;

    public WiggleDreamAnimation(View view) {
        this.view = view;
        this.dir = (.5f - Math.random()) * Math.PI * 2f;
        this.speed = Helpers.px(view.getContext(), new Random().nextInt(195));

        startX = view.getX();
        startY = view.getY();

        setDuration(975);
        setInterpolator(new AccelerateInterpolator());
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        double x = Math.cos(this.dir + interpolatedTime * 1.6);
        double y = Math.sin(this.dir + interpolatedTime * 1.6);

        this.view.setTranslationX(startX + (float) x * interpolatedTime * speed);
        this.view.setTranslationY(startY + (float) y * interpolatedTime * speed);
        this.view.setAlpha(.25f - interpolatedTime * .25f);
    }
}
