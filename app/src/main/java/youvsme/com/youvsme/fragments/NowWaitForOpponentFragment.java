package youvsme.com.youvsme.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import youvsme.com.youvsme.R;
import youvsme.com.youvsme.models.GameModel;
import youvsme.com.youvsme.services.GameService;
import youvsme.com.youvsme.services.StateService;

/**
 * Created by jacob on 4/22/16.
 */
public class NowWaitForOpponentFragment extends GameStateFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_now_wait_for_opponent, null);

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StateService.use().getState().backPressed();
            }
        });

        update(view);

        return view;
    }

    @Override
    public void update() {
        if (getView() != null) {
            update(getView());
        }
    }

    private void update(View view) {
        final GameModel game = GameService.use().latestGame();

        if (view == null || game == null) {
            return;
        }

        Animation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
        animation.setDuration(10000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);
        animation.setInterpolator(new LinearInterpolator());
        view.findViewById(R.id.waiting).startAnimation(animation);

        ImageView opponentPicture = ((ImageView) view.findViewById(R.id.opponentPicture));
        Picasso.with(getContext()).load(game.getOpponent().getPictureUrl()).into(opponentPicture);

        ((TextView) view.findViewById(R.id.opponentName)).setText(getString(R.string.waiting_on_opponent, game.getOpponent().getFirstName()));
    }
}
