package youvsme.com.youvsme.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import youvsme.com.youvsme.R;
import youvsme.com.youvsme.models.GameModel;
import youvsme.com.youvsme.models.QuestionModel;
import youvsme.com.youvsme.services.GameService;
import youvsme.com.youvsme.services.JsonService;
import youvsme.com.youvsme.services.RealmService;
import youvsme.com.youvsme.services.StateService;
import youvsme.com.youvsme.states.SearchForOpponentState;

/**
 * Created by jacob on 3/5/16.
 */
public class QuestionFragment extends GameStateFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_question, null);

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SearchForOpponentState) StateService.use().getState()).backButton();
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

        return view;
    }

    @Override
    public void update() {
        final View view = getView();
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

        QuestionModel question;

        // How many out of the 5 questions I need to pick answers for I have left
        List<QuestionModel> myQuestionsRemaining = RealmService.use().get().where(QuestionModel.class)
                .equalTo("game.id", game.getId())
                .equalTo("user.id", GameService.use().myUserId())
                .isNull("chosenAnswer")
                .findAll();

        // How many questions out of their 5 questions I haven't guessed yet
        List<QuestionModel> opponentsAnswersUnguessed = RealmService.use().get().where(QuestionModel.class)
                .equalTo("game.id", game.getId())
                .equalTo("user.id", game.getOpponent().getId())
                .isNull("opponentsGuess")
                .findAll();

        boolean isMyQuestion;
        int questionNumber;

        if (myQuestionsRemaining.size() != 0) {
            question = myQuestionsRemaining.get(0);
            isMyQuestion = true;
            questionNumber = 5 - myQuestionsRemaining.size();
        } else if (opponentsAnswersUnguessed.size() != 0) {
            question = opponentsAnswersUnguessed.get(0);
            isMyQuestion = false;
            questionNumber = 5 - opponentsAnswersUnguessed.size();
        } else {
            return; // idk wth
        }

        List<String> choices = JsonService.json(question.getChoices(), new TypeToken<List<String>>() {}.getType());

        if (choices.size() < 4) {
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

    private void choose(int choice) {

    }

    private String nameInject(String text, String name) {
        return text;
    }
}
