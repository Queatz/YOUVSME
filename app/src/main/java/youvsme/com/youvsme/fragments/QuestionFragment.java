package youvsme.com.youvsme.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import youvsme.com.youvsme.R;
import youvsme.com.youvsme.models.GameModel;
import youvsme.com.youvsme.models.QuestionModel;
import youvsme.com.youvsme.services.GameService;
import youvsme.com.youvsme.services.JsonService;
import youvsme.com.youvsme.services.SoundService;
import youvsme.com.youvsme.services.StateService;
import youvsme.com.youvsme.states.GameState;
import youvsme.com.youvsme.util.BackgroundAnimator;
import youvsme.com.youvsme.util.Config;

/**
 * Created by jacob on 3/5/16.
 */
public class QuestionFragment extends GameStateFragment {

    private QuestionModel question;
    private Vibrator vibrateor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_question, null);

        vibrateor = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
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

    public void update(final View view) {
        final GameModel game = ((GameState) StateService.use().getState()).getGame();

        if (view == null || game == null) {
            return;
        }

        final View top = view.findViewById(R.id.top);
        final TextView title = (TextView) view.findViewById(R.id.title);
        final ImageView picture = (ImageView) view.findViewById(R.id.opponentPicture);
        final TextView questionText = (TextView) view.findViewById(R.id.questionText);
        final TextView choiceAText = (TextView) view.findViewById(R.id.choiceAText);
        final TextView choiceBText = (TextView) view.findViewById(R.id.choiceBText);
        final TextView choiceCText = (TextView) view.findViewById(R.id.choiceCText);
        final TextView choiceDText = (TextView) view.findViewById(R.id.choiceDText);

        List<QuestionModel> myQuestionsRemaining = GameService.use().myQuestionsRemaining(game);
        List<QuestionModel> opponentsAnswersUnguessed = GameService.use().opponentsAnswersUnguessed(game);

        boolean isMyQuestion;

        if (myQuestionsRemaining.size() != 0) {
            question = myQuestionsRemaining.get(0);
            isMyQuestion = true;
        } else if (opponentsAnswersUnguessed.size() != 0) {
            question = opponentsAnswersUnguessed.get(0);
            isMyQuestion = false;
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

        String userNameInject = game.getUser().getFirstName();
        String opponentNameInject = game.getOpponent().getFirstName();

        if (isMyQuestion) {
            title.setText(getString(R.string.yourQuestions));
            picture.setVisibility(View.GONE);
            questionText.setText(nameInject(question.getText(), userNameInject, opponentNameInject));
            top.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        } else {
            String nameSwitch = opponentNameInject;
            opponentNameInject = userNameInject;
            userNameInject = nameSwitch;

            title.setText(getString(R.string.theirQuestions, game.getOpponent().getFirstName()));
            picture.setVisibility(View.VISIBLE);
            Picasso.get().load(game.getOpponent().getPictureUrl()).into(picture);
            questionText.setText(nameInject(question.getText(), userNameInject, opponentNameInject));
            top.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        view.findViewById(R.id.scrollMe).setScrollY(0);

        view.findViewById(R.id.scrollMe).postDelayed(new Runnable() {
            @Override
            public void run() {
                ((ScrollView) view.findViewById(R.id.scrollMe)).smoothScrollBy(0, 32);

            }
        }, 100);

        choiceAText.setText(nameInject(choices.get(0), userNameInject, opponentNameInject));
        choiceBText.setText(nameInject(choices.get(1), userNameInject, opponentNameInject));
        choiceCText.setText(nameInject(choices.get(2), userNameInject, opponentNameInject));
        choiceDText.setText(nameInject(choices.get(3), userNameInject, opponentNameInject));

        // Shrink long texts' text sizes

        int textSize = 22;

        for (TextView textView : new TextView[] {choiceAText, choiceBText, choiceCText, choiceDText}) {
            textSize = Math.min(textSize, textView.getText().toString().length() < 48 ? 22 : 16);
        }

        for (TextView textView : new TextView[] {choiceAText, choiceBText, choiceCText, choiceDText}) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        }

        questionText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
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

        GameModel game = ((GameState) StateService.use().getState()).getGame();

        if (!GameService.use().isMyQuestion(question, game)) {
            boolean correct = question.getChosenAnswer().equals(choice);
            flash(textViews.get(choice), correct);
            SoundService.use().play(correct ? R.raw.correct : R.raw.wronganswer);
        } else {
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((GameState) StateService.use().getState()).next();
                }
            }, 425);
        }
    }

    private void flash(View view, boolean correct) {
        if (!correct) {
            vibrateor.vibrate(425);
        }

        new BackgroundAnimator(view, correct ? R.color.correct : R.color.incorrect) {
            @Override
            public void onComplete() {
                ((GameState) StateService.use().getState()).next();
            }
        };
    }

    private String nameInject(String text, String user, String opponent) {
        return text.replace("{me}", user).replace("{opponent}", opponent);
    }
}
