package youvsme.com.youvsme.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Date;

import youvsme.com.youvsme.R;
import youvsme.com.youvsme.models.GameModel;
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
                if (dogenimation != null) {
                    return;
                }

                StateService.use().getState().backPressed();
            }
        });

        final TextView kick = (TextView) view.findViewById(R.id.sendKickInTheFaceButton);

        kick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dogenimation != null) {
                    return;
                }

                ViewUtil.battle(kick, 0);
                kick.setText(R.string.bam);
                kick.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        kick.setText(R.string.send_kick_in_the_face);
                    }
                }, 1125);
                ((GameState) StateService.use().getState()).sendKendKickInTheFace();

                ((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(425);
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
        Picasso.get().load(game.getOpponent().getPictureUrl()).into(opponentPicture);

        opponentPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dogenimation != null) {
                    return;
                }

                SoundService.use().play(R.raw.wronganswer);
                ViewUtil.battle(opponentPicture, 0);
                ViewUtil.battle(view.findViewById(R.id.waiting), 150);

                if (dogeness> 0) {
                    dogepower -= new Date().getTime() - dogeness;
                }

                if (dogepower < 0) {
                    dogepower = 0;
                }

                dogepower += 425;

                dogeness = new Date().getTime();

                Log.e("DOGE", "power = " + dogepower);

                if (dogepower > 5000) {
                    doge();
                }
            }
        });

        ((TextView) view.findViewById(R.id.opponentName)).setText(getString(R.string.waiting_on_opponent, game.getOpponent().getFirstName()));
    }

    private Animation dogenimation = null;
    private long dogeness = 0;
    private long dogepower = 0;

    private void doge() {
        if (getView() == null || dogenimation != null) {
            return;
        }

        View doge = getView().findViewById(R.id.doge);
        doge.setVisibility(View.VISIBLE);
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
        animation.setDuration(15432);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dogeReturn();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        dogenimation = animation;

        doge.startAnimation(animation);
    }

    private void dogeReturn() {
        final View doge = getView().findViewById(R.id.doge);
        doge.setVisibility(View.VISIBLE);
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
        animation.setDuration(15432);
        animation.setStartOffset(4654);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                doge.setVisibility(View.GONE);
                dogenimation = null;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        dogenimation = animation;

        doge.startAnimation(animation);
    }
}
