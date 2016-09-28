package youvsme.com.youvsme.util;

import android.view.View;
import android.view.animation.BounceInterpolator;

/**
 * Created by jacob on 9/27/16.
 */

public class ViewUtil {

    public static void battle(View view, int startDelay) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0.f);
        view.setScaleX(1.15f);
        view.setScaleY(1.15f);
        view.animate()
                .setStartDelay(startDelay)
                .setInterpolator(new BounceInterpolator())
                .alpha(1.f)
                .scaleX(1.f).scaleY(1.f)
                .setDuration(750)
                .start();
    }
}
