package youvsme.com.youvsme.states;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import youvsme.com.youvsme.R;
import youvsme.com.youvsme.fragments.FinalResultsFragment;
import youvsme.com.youvsme.fragments.GameStateFragment;
import youvsme.com.youvsme.fragments.LetsGoFragment;
import youvsme.com.youvsme.fragments.QuestionFragment;
import youvsme.com.youvsme.fragments.ScaredOpponentFragment;
import youvsme.com.youvsme.fragments.SeeWhoWonFragment;
import youvsme.com.youvsme.fragments.SendKickInTheFaceFragment;
import youvsme.com.youvsme.models.GameModel;
import youvsme.com.youvsme.models.QuestionModel;
import youvsme.com.youvsme.services.GameService;
import youvsme.com.youvsme.services.RealmService;
import youvsme.com.youvsme.services.StateService;

/**
 * Created by jacob on 3/5/16.
 */
public class GameState implements State {

    private Fragment currentFragment = null;

    private FinalResultsFragment finalResultsFragment = new FinalResultsFragment();
    private LetsGoFragment letsGoFragment = new LetsGoFragment();
    private QuestionFragment questionFragment = new QuestionFragment();
    private ScaredOpponentFragment scaredOpponentFragment = new ScaredOpponentFragment();
    private SeeWhoWonFragment seeWhoWonFragment = new SeeWhoWonFragment();
    private SendKickInTheFaceFragment sendKickInTheFaceFragment = new SendKickInTheFaceFragment();
    private AppCompatActivity activity;

    private GameModel game;

    @Override
    public void show(AppCompatActivity activity) {
        this.activity = activity;
        activity.setContentView(R.layout.activity_search_for_opponent);

        game = GameService.use().latestGame();

        if (game == null) {
            return;
        }

        switch (GameService.use().inferGameState()) {
            case GameService.GAME_STATE_STARTED:
                if (GameService.use().myQuestionsRemaining().size() == 0
                        && GameService.use().opponentsQuestionsRemaining().size() != 0) {
                    //showFragment(theWagerIsSet);
                } else {
                    showFragment(questionFragment);
                }
                break;
            case GameService.GAME_STATE_WAITING_FOR_OPPONENT:
                showFragment(sendKickInTheFaceFragment);
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
    }

    /**
     * User is ready for the next question.
     */
    public void answerQuestion(QuestionModel question, int answer) {
        boolean isMine = question.getUser().getId().equals(game.getUser().getId());

        if (isMine) {
            GameService.use().answerQuestion(question, answer);
        } else {
            GameService.use().guessAnswer(question, answer);
        }

        next();
    }

    /**
     * User wants to move forward.
     */
    public void next() {
        List<QuestionModel> myQuestionsRemaining = GameService.use().myQuestionsRemaining();
        List<QuestionModel> opponentsQuestionsRemaining = GameService.use().opponentsQuestionsRemaining();
        List<QuestionModel> myAnswersUnguessed = GameService.use().myAnswersUnguessed();
        List<QuestionModel> opponentsAnswersUnguessed = GameService.use().opponentsAnswersUnguessed();

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

        if (opponentsAnswersUnguessed.size() == 5) {
            showFragment(letsGoFragment);
        } else if (opponentsQuestionsRemaining.size() > 0) {
            showFragment(sendKickInTheFaceFragment);
        } else if (opponentsAnswersUnguessed.size() == 0 && myAnswersUnguessed.size() > 0) {
            showFragment(sendKickInTheFaceFragment);
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

        GameService.use().sendKickInTheFaceReminder(game, new Runnable() {
            @Override
            public void run() {
                showFragment(scaredOpponentFragment);
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
        if (GameService.use().opponentsQuestionsRemaining().size() > 0) {
            showFragment(sendKickInTheFaceFragment);
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
        return false;
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
}
