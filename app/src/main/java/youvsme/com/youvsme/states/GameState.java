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

        GameService.GameState state = GameService.use().getState();

        switch (state) {
            case PICKING_ANSWERS:
            case GUESSING_OPPONENTS_ANSWERS:
                showFragment(questionFragment);
                break;
            case WAITING_FOR_OPPONENT:
                showFragment(sendKickInTheFaceFragment);
                break;
            case LAST_GAME_FINISHED:
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
    public void answerQustion(QuestionModel question, int answer) {
        next();
    }

    /**
     * User wants to move forward.
     */
    public void next() {

        // How many out of the 5 questions I need to pick answers for I have left
        List<QuestionModel> myQuestionsRemaining = RealmService.use().get().where(QuestionModel.class)
                .equalTo("game.id", game.getId())
                .equalTo("user.id", GameService.use().myUserId())
                .isNull("chosenAnswer")
                .findAll();

        // How many out of the 5 questions my opponent needs to pick answers for they have left
        List<QuestionModel> opponentsQuestionsRemaining = RealmService.use().get().where(QuestionModel.class)
                .equalTo("game.id", game.getId())
                .equalTo("user.id", game.getOpponent().getId())
                .isNull("chosenAnswer")
                .findAll();

        // How many questions out of my 5 questions the opponent hasn't guessed yet
        List<QuestionModel> myAnswersUnguessed = RealmService.use().get().where(QuestionModel.class)
                .equalTo("game.id", game.getId())
                .equalTo("user.id", GameService.use().myUserId())
                .isNull("opponentsGuess")
                .findAll();

        // How many questions out of their 5 questions I haven't guessed yet
        List<QuestionModel> opponentsAnswersUnguessed = RealmService.use().get().where(QuestionModel.class)
                .equalTo("game.id", game.getId())
                .equalTo("user.id", game.getOpponent().getId())
                .isNull("opponentsGuess")
                .findAll();

        // TODO don't show my opponent's answers at all until they have finished all of them
        // TODO could be done on the server

        // No more actions for me to take
        boolean iAmDone = myQuestionsRemaining.size() == 0 && opponentsAnswersUnguessed.size() == 0;

        // No more actions for the opponent to take
        boolean opponentIsDone = opponentsQuestionsRemaining.size() == 0 && myAnswersUnguessed.size() == 0;

        if (iAmDone && opponentIsDone) {
            GameService.use().setUserHasSeenFinalResults(false);
            showFragment(seeWhoWonFragment);
        } else if (iAmDone) {
            if (currentFragment == letsGoFragment) {
                showFragment(sendKickInTheFaceFragment);
            } else {
                showFragment(letsGoFragment);
            }
        } else if (myQuestionsRemaining.size() != 0) { // Still need to pick my answers
            showFragment(null /* The wager is set: {{wager}} First, answer 5 questions. */);
        } else if (opponentsAnswersUnguessed.size() != 0) {
            showFragment(questionFragment); // Still need to answer theirs
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
        showFragment(questionFragment);
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
        if (currentFragment == null) {
            return true; // TODO some go back?
        }

        return false;
    }

    public void playAgain() {
        GameService.use().setUserHasClickedPlayAgain(true);
        StateService.use().go(new SearchForOpponentState());
    }
}
