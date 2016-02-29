package youvsme.com.youvsme.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import io.realm.Sort;
import youvsme.com.youvsme.YOUVSMEApp;
import youvsme.com.youvsme.models.GameModel;
import youvsme.com.youvsme.models.UserModel;
import youvsme.com.youvsme.util.Config;

/**
 * Created by jacob on 2/28/16.
 */
public class GameService {

    private static GameService instance;
    public static GameService use() {
        if (instance == null) {
            synchronized (GameService.class) {
                if (instance == null) {
                    instance = new GameService();
                }
            }
        }

        return instance;
    }

    private Context context;
    private SharedPreferences preferences;

    public void initialize(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Context context() {
        return context;
    }

    public enum GameState {
        NO_USER,
        NO_OPPONENT,
        ANSWERING_QUESTIONS,
        WAITING_FOR_OPPONENT,
        LAST_GAME_FINISHED
    }

    public GameState getState() {
        final UserModel user = currentUser();

        if (user == null) {
            return GameState.NO_USER;
        }

        // Great, they're logged in

        final GameModel game = latestGame();

        if (game == null) {
            return GameState.NO_OPPONENT;
        }

        // Great, they already have a game going with somebody

        switch (game.getState()) {
            case GameModel.GAME_STATE_STARTED:
                return GameState.ANSWERING_QUESTIONS;
            case GameModel.GAME_STATE_WAITING_FOR_OPPONENT:
                return GameState.WAITING_FOR_OPPONENT;
            case GameModel.GAME_STATE_FINISHED:
            default:
                if (userHasClickedPlayAgain()) {
                    return GameState.NO_OPPONENT;
                } else {
                    return GameState.LAST_GAME_FINISHED;
                }
        }
    }

    // TODO make sure this gets set to false when the game finishes
    public boolean userHasClickedPlayAgain() {
        return preferences.getBoolean(Config.PREF_PLAY_AGAIN, false);
    }

    public void setUserHasClickedPlayAgain(boolean value) {
        preferences.edit().putBoolean(Config.PREF_PLAY_AGAIN, value).apply();
    }

    @Nullable
    public GameModel latestGame() {
        if (myUserId() == null) {
            return null;
        }

        return RealmService.use().get()
                .where(GameModel.class)
                .equalTo("user.id", myUserId())
                .notEqualTo("state", GameModel.GAME_STATE_FINISHED)
                .findAllSorted("started", Sort.DESCENDING).first();
    }

    @Nullable
    public UserModel currentUser() {
        return RealmService.use().get()
                .where(UserModel.class)
                .equalTo("id", myUserId())
                .findFirst();
    }

    @Nullable
    public String myUserId() {
        return preferences.getString(Config.PREF_MY_USER_ID, null);
    }

    @Nullable
    public String myUserToken() {
        return preferences.getString(Config.PREF_MY_USER_TOKEN, null);
    }
}
