package youvsme.com.youvsme.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.realm.RealmResults;
import io.realm.Sort;
import youvsme.com.youvsme.models.GameModel;
import youvsme.com.youvsme.models.QuestionModel;
import youvsme.com.youvsme.models.UserModel;
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
        /**
         * User is not signed in at all.
         */
        NO_USER,

        /**
         * User is signed in but has never started a game.
         */
        NO_OPPONENT,

        /**
         * User is in an active game.
         */
        IN_GAME,

        /**
         * The last game was finished. User can see the last game's score.
         */
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
            case GameModel.GAME_STATE_WAITING_FOR_OPPONENT:
            case GameModel.GAME_STATE_GUESSING_OPPONENTS_ANSWERS:
                return GameState.IN_GAME;
            case GameModel.GAME_STATE_FINISHED:
            default:
                if (userHasSeenFinalResults()) {
                    return GameState.NO_OPPONENT;
                } else {
                    return GameState.LAST_GAME_FINISHED;
                }
        }
    }

    public boolean userHasSeenFinalResults() {
        return preferences.getBoolean(Config.PREF_USER_HAS_SEEN_FINAL_RESULTS, false);
    }

    public boolean userHasClickedPlayAgain() {
        return preferences.getBoolean(Config.PREF_USER_HAS_CLICKED_PLAY_AGAIN, false);
    }

    public void setUserHasSeenFinalResults(boolean value) {
        preferences.edit().putBoolean(Config.PREF_USER_HAS_SEEN_FINAL_RESULTS, value).apply();
    }

    public void setUserHasClickedPlayAgain(boolean value) {
        preferences.edit().putBoolean(Config.PREF_USER_HAS_CLICKED_PLAY_AGAIN, value).apply();
    }

    public void sendKickInTheFaceReminder(final GameModel game, final Runnable callback) {
        ApiService.use().get("game/" + game.getId() + "/kick-in-the-face", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                callback.run();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context(), "Your roundhouse kick to the face failed due to a network issue. Yor kick to the face, however, was glorious.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loadGame(final Runnable callback) {
        ApiService.use().get("me/game", null, new RealmObjectResponseHandler<GameModel>() {
            @Override
            public void success(GameModel response) {
                callback.run();
            }

            @Override
            public void failure(int statusCode, String response) {

            }
        });
    }

    public void answerQuestion(QuestionModel question, int answer, final Runnable callback) {
        // TODO: if this is the last one, send network

        ApiService.use().post("game/" + question.getGame().getId() + "/answer/" + question.getId() + "/" + answer, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                callback.run();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    public void guessAnswer(QuestionModel question, int guess, final Runnable callback) {
        // TODO: if this is the last one, send network

        ApiService.use().post("game/" + question.getGame().getId() + "/guess/" + question.getId() + "/" + guess, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                callback.run();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
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
