package youvsme.com.youvsme.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import java.util.List;

import youvsme.com.youvsme.R;
import youvsme.com.youvsme.models.GameModel;
import youvsme.com.youvsme.models.QuestionModel;
import youvsme.com.youvsme.models.UserModel;
import youvsme.com.youvsme.services.GameService;
import youvsme.com.youvsme.services.SoundService;
import youvsme.com.youvsme.services.StateService;
import youvsme.com.youvsme.states.GameState;

/**
 * Created by jacob on 3/5/16.
 */
public class FinalResultsFragment extends GameStateFragment {

    private boolean showingWager = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_final_results, null);

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StateService.use().getState().backPressed();
            }
        });

        view.findViewById(R.id.playAgainButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GameState) StateService.use().getState()).playAgain();
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

    public void update(final View view) {
        final TextView yourFinalScore = (TextView) view.findViewById(R.id.yourFinalScore);
        final TextView opponentsFinalScore = (TextView) view.findViewById(R.id.opponentsFinalScore);
        final TextView yourName = (TextView) view.findViewById(R.id.yourName);
        final TextView opponentsName = (TextView) view.findViewById(R.id.opponentsName);

        if (!StateService.use().is(GameState.class)) {
            return;
        }

        final GameModel game = ((GameState) StateService.use().getState()).getGame();

        if (game == null) {
            return;
        }

        // Important! intentionally leave out which ones were right and wrong so that they have to ask each other

        UserModel me = game.getUser();
        UserModel opponent = game.getOpponent();

        if (me == null || opponent == null) {
            return;
        }

        int usersCorrect = GameService.use().numberOfMyGuessesCorrect(game);
        int opponentsCorrect = GameService.use().numberOfOpponentsGuessesCorrect(game);

        yourFinalScore.setText("0" + usersCorrect);
        opponentsFinalScore.setText("0" + opponentsCorrect);

        yourName.setText(me.getFirstName());
        opponentsName.setText(opponent.getFirstName());

        if (usersCorrect > opponentsCorrect) {
            SoundService.use().play(R.raw.youwon);
        } else if (opponentsCorrect > usersCorrect) {
            SoundService.use().play(R.raw.youlostthebattle);
        } else {
            SoundService.use().play(R.raw.correct);
        }

        setGameWinLoseText(view);

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                swingOut(view);
            }
        }, 1500);
    }

    private void swingIn(final View view) {
        if (view == null || !isAdded()) {
            return;
        }

        if (!StateService.use().is(GameState.class)) {
            return;
        }

        final GameModel game = ((GameState) StateService.use().getState()).getGame();

        if (game == null) {
            return;
        }

        // Important! intentionally leave out which ones were right and wrong so that they have to ask each other

        UserModel me = game.getUser();
        UserModel opponent = game.getOpponent();

        if (me == null || opponent == null) {
            return;
        }

        final TextView winnerText = (TextView) view.findViewById(R.id.winnerText);
        final TextView loserText = (TextView) view.findViewById(R.id.loserText);

        if (showingWager) {
            if (game.getWager() == null) {
                winnerText.setText(getString(R.string.no_wager_was_set));
            } else {
                winnerText.setText(game.getWager() != null ? game.getWager() : "");
            }

            loserText.setText(game.getWagerNote() != null ? game.getWagerNote() : "");
        } else {
            setGameWinLoseText(view);
        }

        animateIn(winnerText, new Runnable() {
            @Override
            public void run() {
                swingOut(getView());
            }
        });
        animateIn(loserText, null);

        showingWager = !showingWager;
    }

    private void setGameWinLoseText(View view) {
        if (view == null || !isAdded()) {
            return;
        }

        if (!StateService.use().is(GameState.class)) {
            return;
        }

        final GameModel game = ((GameState) StateService.use().getState()).getGame();

        if (game == null) {
            return;
        }

        // Important! intentionally leave out which ones were right and wrong so that they have to ask each other

        UserModel me = game.getUser();
        UserModel opponent = game.getOpponent();

        if (me == null || opponent == null) {
            return;
        }

        final TextView winnerText = (TextView) view.findViewById(R.id.winnerText);
        final TextView loserText = (TextView) view.findViewById(R.id.loserText);

        int usersCorrect = GameService.use().numberOfMyGuessesCorrect(game);
        int opponentsCorrect = GameService.use().numberOfOpponentsGuessesCorrect(game);

        if (usersCorrect > opponentsCorrect) {
            winnerText.setText(getString(R.string.you_defeated_them, opponent.getFirstName()));
            loserText.setText(getString(R.string.no_match, pronounFromGender(opponent.getGender())));
        } else if (opponentsCorrect > usersCorrect) {
            winnerText.setText(getString(R.string.they_smoked_you, opponent.getFirstName()));
            loserText.setText(getString(R.string.your_time_will_come));
        } else {
            winnerText.setText(getString(R.string.youve_tied));
            loserText.setText(getString(R.string.equilibrium));
        }
    }

    private void swingOut(final View view) {
        if (view == null || !isAdded()) {
            return;
        }

        final TextView winnerText = (TextView) view.findViewById(R.id.winnerText);
        final TextView loserText = (TextView) view.findViewById(R.id.loserText);

        animateOut(winnerText, new Runnable() {
            @Override
            public void run() {
                swingIn(getView());
            }
        });
        animateOut(loserText, null);
    }

    private String pronounFromGender(String gender) {
        switch (gender) {
            case "female":
                return "She";
            case "male":
                return "He";
            default:
                return "They";
        }
    }

    private void animateOut(View view, Runnable endAction) {
        view.animate()
                .alpha(0)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(1500)
                .withEndAction(endAction)
                .start();
    }

    private void animateIn(View view, Runnable endAction) {
        view.animate()
                .alpha(1)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(1500)
                .withEndAction(endAction)
                .start();
    }
}
