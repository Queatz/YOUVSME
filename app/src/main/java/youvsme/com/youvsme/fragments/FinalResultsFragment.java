package youvsme.com.youvsme.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import youvsme.com.youvsme.R;
import youvsme.com.youvsme.models.GameModel;
import youvsme.com.youvsme.models.QuestionModel;
import youvsme.com.youvsme.models.UserModel;
import youvsme.com.youvsme.services.GameService;
import youvsme.com.youvsme.services.RealmService;
import youvsme.com.youvsme.services.StateService;
import youvsme.com.youvsme.states.GameState;

/**
 * Created by jacob on 3/5/16.
 */
public class FinalResultsFragment extends GameStateFragment {
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

        view.post(new Runnable() {
            @Override
            public void run() {
                update();
            }
        });

        return view;
    }

    @Override
    public void update() {
        final View view = getView();

        if (view == null) {
            return;
        }

        final TextView yourFinalScore = (TextView) view.findViewById(R.id.yourFinalScore);
        final TextView opponentsFinalScore = (TextView) view.findViewById(R.id.opponentsFinalScore);
        final TextView yourName = (TextView) view.findViewById(R.id.yourName);
        final TextView opponentsName = (TextView) view.findViewById(R.id.opponentsName);
        final TextView winnerText = (TextView) view.findViewById(R.id.winnerText);
        final TextView loserText = (TextView) view.findViewById(R.id.loserText);

        final GameModel game = GameService.use().latestGame();

        if (game == null) {
            return;
        }

        // Important! intentionally leave out which ones were right and wrong so that they have to ask each other

        UserModel me = game.getUser();
        UserModel opponent = game.getOpponent();

        if (me == null || opponent == null) {
            return;
        }

        int usersCorrect = 0;
        int opponentsCorrect = 0;

        List<QuestionModel> myQuestions = GameService.use().myQuestions();

        List<QuestionModel> opponentsQuestions = GameService.use().opponentsQuestions();

        // Aggregate user's correct guesses
        for (QuestionModel question : opponentsQuestions) {
            if (question.getOpponentsGuess() == null || question.getChosenAnswer() == null) {
                return;
            }

            if (question.getOpponentsGuess().equals(question.getChosenAnswer())) {
                usersCorrect++;
            }
        }

        // Aggregate opponents correct guesses
        for (QuestionModel question : myQuestions) {
            if (question.getOpponentsGuess() == null || question.getChosenAnswer() == null) {
                return;
            }

            if (question.getOpponentsGuess().equals(question.getChosenAnswer())) {
                opponentsCorrect++;
            }
        }

        yourFinalScore.setText("0" + usersCorrect);
        opponentsFinalScore.setText("0" + opponentsCorrect);

        yourName.setText(me.getFirstName());
        opponentsName.setText(opponent.getFirstName());

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
}
