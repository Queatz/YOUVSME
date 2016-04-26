package youvsme.com.youvsme.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import youvsme.com.youvsme.R;
import youvsme.com.youvsme.models.GameModel;
import youvsme.com.youvsme.models.QuestionModel;
import youvsme.com.youvsme.services.GameService;
import youvsme.com.youvsme.services.JsonService;
import youvsme.com.youvsme.services.StateService;
import youvsme.com.youvsme.states.GameState;
import youvsme.com.youvsme.util.BackgroundAnimator;
import youvsme.com.youvsme.util.Config;
import youvsme.com.youvsme.util.CorrectCallback;

/**
 * Created by jacob on 3/5/16.
 */
public class QuestionFragment extends GameStateFragment {

    private QuestionModel question;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_question, null);

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GameState) StateService.use().getState()).backPressed();
            }
        });

        final View choiceAView = view.findViewById(R.id.choiceAView);
        final View choiceBView = view.findViewById(R.id.choiceBView);
        final View choiceCView = view.findViewById(R.id.choiceCView);
        final View choiceDView = view.findViewById(R.id.choiceDView);

        choiceAView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose(0);
            }
        });

        choiceBView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose(1);
            }
        });

        choiceCView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose(2);
            }
        });

        choiceDView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose(3);
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

    public void update(View view) {
        final GameModel game = GameService.use().latestGame();

        if (view == null || game == null) {
            return;
        }

        final TextView title = (TextView) view.findViewById(R.id.title);
        final TextView questionText = (TextView) view.findViewById(R.id.questionText);
        final TextView choiceAText = (TextView) view.findViewById(R.id.choiceAText);
        final TextView choiceBText = (TextView) view.findViewById(R.id.choiceBText);
        final TextView choiceCText = (TextView) view.findViewById(R.id.choiceCText);
        final TextView choiceDText = (TextView) view.findViewById(R.id.choiceDText);

        List<QuestionModel> myQuestionsRemaining = GameService.use().myQuestionsRemaining();
        List<QuestionModel> opponentsAnswersUnguessed = GameService.use().opponentsAnswersUnguessed();

        boolean isMyQuestion;
        int questionNumber;

        if (myQuestionsRemaining.size() != 0) {
            question = myQuestionsRemaining.get(0);
            isMyQuestion = true;
            questionNumber = 5 - myQuestionsRemaining.size() + 1;
        } else if (opponentsAnswersUnguessed.size() != 0) {
            question = opponentsAnswersUnguessed.get(0);
            isMyQuestion = false;
            questionNumber = 5 - opponentsAnswersUnguessed.size() + 1;
        } else {
            Log.w(Config.LOGGER, "Nothing to do here, skipping.");
            ((GameState) StateService.use().getState()).next();
            return;
        }

        List<String> choices = JsonService.json(question.getChoices(), new TypeToken<List<String>>() {}.getType());

        if (choices.size() < 4) {
            Log.w(Config.LOGGER, "Got question with less than 4 choices, dying.");
            ((GameState) StateService.use().getState()).next();
            return;
        }

        if (isMyQuestion) {
            title.setText(getString(R.string.question_x, questionNumber));
            questionText.setText(nameInject(question.getText(), null));
        } else {
            title.setText(getString(R.string.answer_x, questionNumber));
            questionText.setText(nameInject(question.getText(), game.getOpponent().getFirstName()));
        }

        choiceAText.setText(choices.get(0));
        choiceBText.setText(choices.get(1));
        choiceCText.setText(choices.get(2));
        choiceDText.setText(choices.get(3));
    }

    private void choose(final int choice) {
        View view = getView();

        if (view == null) {
            return;
        }

        final List<View> textViews = new ArrayList<>();
        textViews.add(view.findViewById(R.id.choiceAView));
        textViews.add(view.findViewById(R.id.choiceBView));
        textViews.add(view.findViewById(R.id.choiceCView));
        textViews.add(view.findViewById(R.id.choiceDView));

        if (question == null) {
            Log.w(Config.LOGGER, "Tried to answer without a question, skipping.");
            ((GameState) StateService.use().getState()).next();
            return;
        }

        ((GameState) StateService.use().getState()).answerQuestion(question, choice);

        if (!GameService.use().isMyQuestion(question, GameService.use().latestGame())) {
            flash(textViews.get(choice), question.getChosenAnswer().equals(choice));
        } else {
            ((GameState) StateService.use().getState()).next();
        }
    }

    private void flash(View view, boolean correct) {
        new BackgroundAnimator(view, correct ? R.color.correct : R.color.incorrect) {
            @Override
            public void onComplete() {
                ((GameState) StateService.use().getState()).next();
            }
        };
    }

    private String nameInject(String text, String name) {
        return text;
    }
}
