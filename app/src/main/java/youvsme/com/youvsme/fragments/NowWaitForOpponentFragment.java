package youvsme.com.youvsme.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import youvsme.com.youvsme.R;
import youvsme.com.youvsme.models.GameModel;
import youvsme.com.youvsme.services.GameService;
import youvsme.com.youvsme.services.SoundService;
import youvsme.com.youvsme.services.StateService;
import youvsme.com.youvsme.states.GameState;
import youvsme.com.youvsme.util.ViewUtil;

/**
 * Created by jacob on 4/22/16.
 */
public class NowWaitForOpponentFragment extends GameStateFragment {
    boolean isInitial;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_now_wait_for_opponent, null);

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StateService.use().getState().backPressed();
            }
        });

        view.findViewById(R.id.sendKickInTheFaceButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GameState) StateService.use().getState()).sendKendKickInTheFace();
            }
        });

        update(view);

        return view;
    }

    public void setInitial(boolean isInitial) {
        this.isInitial = isInitial;
    }

    @Override
    public void update() {
        if (getView() != null) {
            update(getView());
        }
    }

    private void update(final View view) {
        final GameModel game = ((GameState) StateService.use().getState()).getGame();

        if (view == null || game == null) {
            return;
        }

        if (isInitial) {
            view.findViewById(R.id.bottomLayout).setVisibility(View.GONE);
        }

        Animation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
        animation.setDuration(10000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);
        animation.setInterpolator(new LinearInterpolator());
        view.findViewById(R.id.waiting).startAnimation(animation);

        final ImageView opponentPicture = ((ImageView) view.findViewById(R.id.opponentPicture));
        Picasso.with(getContext()).load(game.getOpponent().getPictureUrl()).into(opponentPicture);

        opponentPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundService.use().play(R.raw.wronganswer);
                ViewUtil.battle(opponentPicture, 0);
                ViewUtil.battle(view.findViewById(R.id.waiting), 150);
            }
        });

        ((TextView) view.findViewById(R.id.opponentName)).setText(getString(R.string.waiting_on_opponent, game.getOpponent().getFirstName()));
    }
}
