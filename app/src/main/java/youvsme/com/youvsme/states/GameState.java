package youvsme.com.youvsme.states;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.List;

import youvsme.com.youvsme.R;
import youvsme.com.youvsme.fragments.FinalResultsFragment;
import youvsme.com.youvsme.fragments.GameStateFragment;
import youvsme.com.youvsme.fragments.LetsGoFragment;
import youvsme.com.youvsme.fragments.NowWaitForOpponentFragment;
import youvsme.com.youvsme.fragments.QuestionFragment;
import youvsme.com.youvsme.fragments.SeeWhoWonFragment;
import youvsme.com.youvsme.fragments.WagerReviewFragment;
import youvsme.com.youvsme.models.GameModel;
import youvsme.com.youvsme.models.QuestionModel;
import youvsme.com.youvsme.services.GameService;
import youvsme.com.youvsme.services.StateService;

/**
 * Created by jacob on 3/5/16.
 */
public class GameState implements State {

    private Fragment currentFragment = null;

    private FinalResultsFragment finalResultsFragment = new FinalResultsFragment();
    private LetsGoFragment letsGoFragment = new LetsGoFragment();
    private NowWaitForOpponentFragment waitingForMai = new NowWaitForOpponentFragment();
    private QuestionFragment questionFragment = new QuestionFragment();
    private SeeWhoWonFragment seeWhoWonFragment = new SeeWhoWonFragment();
    private WagerReviewFragment theWagerIsSet = new WagerReviewFragment();
    private AppCompatActivity activity;

    private GameModel game;

    public GameState(GameModel game) {
        this.game = game;
    }

    @Override
    public void show(AppCompatActivity activity) {
        this.activity = activity;
        activity.setContentView(R.layout.activity_search_for_opponent);

        if (game == null) {
            return;
        }

        GameService.use().setUserHasClickedPlayAgain(false);

        switch (GameService.use().inferGameState(game)) {
            case GameService.GAME_STATE_STARTED:
                if (game.getWager() != null
                        && game.getWager().length() > 0
                        && GameService.use().myQuestionsRemaining(game).size() == GameService.use().numberOfQuestions(game)
                        && GameService.use().opponentsQuestionsRemaining(game).size() == 0) {
                    showFragment(theWagerIsSet);
                } else {
                    showFragment(questionFragment);
                }
                break;
            case GameService.GAME_STATE_WAITING_FOR_OPPONENT:
                waitingForMai.setInitial(false);
                showFragment(waitingForMai);
                break;
            case GameService.GAME_STATE_GUESSING_OPPONENTS_ANSWERS:
                showFragment(questionFragment);
                break;
            case GameService.GAME_STATE_FINISHED:
                if (GameService.use().userHasSeenFinalResults()) {
                    showFragment(finalResultsFragment);
                } else {
                    showFragment(seeWhoWonFragment);
                }

                break;
        }
    }


    /**
     * Game begins.
     */
    public void itBegins() {
        GameService.use().setUserHasSeenFinalResults(false);
        GameService.use().setUserHasClickedPlayAgain(false);
    }

    /**
     * User is ready for the next question.
     */
    public void answerQuestion(QuestionModel question, int answer) {
        if (GameService.use().isMyQuestion(question, game)) {
            GameService.use().answerQuestion(game, question, answer);
        } else {
            GameService.use().guessAnswer(game, question, answer);
        }
    }

    /**
     * User wants to move forward.
     */
    public void next() {
        List<QuestionModel> myQuestionsRemaining = GameService.use().myQuestionsRemaining(game);
        List<QuestionModel> opponentsQuestionsRemaining = GameService.use().opponentsQuestionsRemaining(game);
        List<QuestionModel> myAnswersUnguessed = GameService.use().myAnswersUnguessed(game);
        List<QuestionModel> opponentsAnswersUnguessed = GameService.use().opponentsAnswersUnguessed(game);

        // TODO don't show my opponent's answers at all until they have finished all of them
        // TODO could be done on the server

        boolean iAmDone = myQuestionsRemaining.size() == 0 && opponentsAnswersUnguessed.size() == 0;
        boolean opponentIsDone = opponentsQuestionsRemaining.size() == 0 && myAnswersUnguessed.size() == 0;

        if (iAmDone && opponentIsDone) {
            GameService.use().setUserHasSeenFinalResults(false);
            showFragment(seeWhoWonFragment);
            return;

        }

        if (myQuestionsRemaining.size() > 0) {
            showFragment(questionFragment);
            return;
        }

        if (opponentsAnswersUnguessed.size() == GameService.use().numberOfQuestions(game)) {
            showFragment(letsGoFragment);
        } else if (opponentsQuestionsRemaining.size() > 0) {
            waitingForMai.setInitial(true);
            showFragment(waitingForMai);
        } else if (opponentsAnswersUnguessed.size() == 0 && myAnswersUnguessed.size() > 0) {
            waitingForMai.setInitial(true);
            showFragment(waitingForMai);
        } else {
            showFragment(questionFragment);
        }
    }

    /**
     * User wants to kick opponent in the face.
     */
    public void sendKendKickInTheFace() {
        if (game == null) {
            return;
        }

        Toast.makeText(activity, activity.getString(R.string.bam), Toast.LENGTH_SHORT).show();

        GameService.use().sendKickInTheFaceReminder(game, new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    /**
     * User wants to see who won.
     */
    public void seeWhoWon() {
        GameService.use().setUserHasSeenFinalResults(true);
        GameService.use().setUserHasClickedPlayAgain(false);
        showFragment(finalResultsFragment);
    }

    /**
     * User wants to gooooooo.
     */
    public void letsGo() {
        if (GameService.use().opponentsQuestionsRemaining(game).size() > 0) {
            waitingForMai.setInitial(true);
            showFragment(waitingForMai);
        } else {
            showFragment(questionFragment);
        }
    }

    private void showFragment(final GameStateFragment fragment) {
        currentFragment = fragment;
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
        fragment.update();
    }

    @Override
    public boolean back() {
        StateService.use().go(new SearchForOpponentState());
        return true;
    }

    public void playAgain() {
        GameService.use().setUserHasClickedPlayAgain(true);
        StateService.use().go(new SearchForOpponentState());
    }

    @Override
    public void backPressed() {
        if(!back()) {
            activity.onBackPressed();
        }
    }

    public GameModel getGame() {
        return game;
    }
}
