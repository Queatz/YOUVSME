package youvsme.com.youvsme.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;

import youvsme.com.youvsme.R;
import youvsme.com.youvsme.util.ViewUtil;

/**
 * Created by jacob on 2/24/16.
 */
public class EntranceFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrance, null);

        view.findViewById(R.id.logoWithText)
                .animate()
                .setInterpolator(new BounceInterpolator())
                .setStartDelay(350)
                .setDuration(425)
                .alpha(1)
                .scaleX(1)
                .scaleY(1)
                .start();


        return view;
    }
}
