package youvsme.com.youvsme.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;

import java.util.List;
import java.util.logging.Logger;

import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import youvsme.com.youvsme.YOUVSMEApp;
import youvsme.com.youvsme.models.GameModel;
import youvsme.com.youvsme.models.UserModel;
import youvsme.com.youvsme.states.SearchForOpponentState;
import youvsme.com.youvsme.util.Config;
import youvsme.com.youvsme.util.RealmListResponseHandler;
import youvsme.com.youvsme.util.RealmObjectResponseHandler;

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
    public RealmResults<UserModel> getFriends() {
        String myToken = myUserToken();

        if (myToken == null || myUserId() == null) {
            Log.w(Config.LOGGER, "Tried to get friends but no user");
            return null;
        }

        RequestParams params = new RequestParams();
        params.put(Config.PARAM_TOKEN, myToken);

        ApiService.use().get("me/friends", params, new RealmListResponseHandler<UserModel>() {
            @Override
            public void success(List<UserModel> user) {
                Log.w(Config.LOGGER, "found users");
                // They'll've been added to the realm by here
            }

            @Override
            public void failure(int statusCode, String response) {
                Toast.makeText(GameService.use().context(), "There was an error. Bummer.", Toast.LENGTH_SHORT).show();
            }
        });

        return RealmService.use().get()
                .where(UserModel.class)
                .notEqualTo("id", myUserId())
                .findAllSorted("firstName", Sort.ASCENDING);
    }

    @Nullable
    public GameModel latestGame() {
        if (myUserId() == null) {
            return null;
        }

        RealmResults<GameModel> games = RealmService.use().get()
                .where(GameModel.class)
                .equalTo("user.id", myUserId())
                .notEqualTo("state", GameModel.GAME_STATE_FINISHED)
                .findAllSorted("started", Sort.DESCENDING);

        if (games.isEmpty()) {
            return null;
        }

        return games.first();
    }

    @Nullable
    public UserModel currentUser() {
        if (myUserId() == null) {
            return null;
        }

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

    public GameService setMyUserId(final String myUserId) {
        preferences.edit().putString(Config.PREF_MY_USER_ID, myUserId).apply();
        return this;
    }

    public GameService setMyUserToken(final String myUserToken) {
        preferences.edit().putString(Config.PREF_MY_USER_TOKEN, myUserToken).apply();
        return this;
    }
}
