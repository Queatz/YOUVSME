package youvsme.com.youvsme.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.common.collect.ImmutableList;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import youvsme.com.youvsme.R;
import youvsme.com.youvsme.models.GameModel;
import youvsme.com.youvsme.models.QuestionModel;
import youvsme.com.youvsme.models.UserModel;
import youvsme.com.youvsme.util.Config;
import youvsme.com.youvsme.util.RealmObjectResponseHandler;

/**
 * Created by jacob on 2/28/16.
 */
public class GameService {

    public static final String GAME_STATE_STARTED = "started";
    public static final String GAME_STATE_WAITING_FOR_OPPONENT = "waiting";
    public static final String GAME_STATE_GUESSING_OPPONENTS_ANSWERS = "answering";
    public static final String GAME_STATE_FINISHED = "finished";

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

    public boolean isMyQuestion(QuestionModel question, GameModel game) {
        return question.getUser().getId().equals(game.getUser().getId());
    }

    public int numberOfMyGuessesCorrect(GameModel game) {
        int usersCorrect = 0;
        List<QuestionModel> opponentsQuestions = GameService.use().opponentsQuestions(game);

        // Aggregate user's correct guesses
        for (QuestionModel question : opponentsQuestions) {
            if (question.getOpponentsGuess() == null || question.getChosenAnswer() == null) {
                return -1;
            }

            if (question.getOpponentsGuess().equals(question.getChosenAnswer())) {
                usersCorrect++;
            }
        }

        return usersCorrect;
    }

    public int numberOfOpponentsGuessesCorrect(GameModel game) {
        int opponentsCorrect = 0;
        List<QuestionModel> myQuestions = GameService.use().myQuestions(game);

        // Aggregate opponents correct guesses
        for (QuestionModel question : myQuestions) {
            if (question.getOpponentsGuess() == null || question.getChosenAnswer() == null) {
                return 0;
            }

            if (question.getOpponentsGuess().equals(question.getChosenAnswer())) {
                opponentsCorrect++;
            }
        }

        return opponentsCorrect;
    }

    public int numberOfQuestions(GameModel game) {
        return game.getQuestions().size() / 2;
    }

    public void invite() {
        String text = context.getString(R.string.invite_message) + " " + Config.INVITE_URL;
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, text);

        Intent chooserIntent = Intent.createChooser(sharingIntent, context().getResources().getString(R.string.share_using));
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context().startActivity(chooserIntent);
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

        boolean gameNotFinished = false;

        for (QuestionModel q : game.getQuestions()) {
            if (q.getChosenAnswer() == null || q.getOpponentsGuess() == null) {
                gameNotFinished = true;
                break;
            }
        }

        if (gameNotFinished) {
            return GameState.IN_GAME;
        }

        if (userHasClickedPlayAgain()) {
            return GameState.NO_OPPONENT;
        } else {
            return GameState.LAST_GAME_FINISHED;
        }
    }

    public String inferGameState(GameModel game) {
        if (game == null) {
            return null;
        }

        boolean stillAnswering = false;
        boolean stillGuessing = false;
        boolean opponentStillGuessing = false;
        boolean opponentStillAnswering = false;

        for (QuestionModel q : game.getQuestions()) {
            if (myUserId().equals(q.getUser().getId())) {
                if (q.getChosenAnswer() == null) {
                    stillAnswering = true;
                }

                if (q.getOpponentsGuess() == null) {
                    opponentStillGuessing = true;
                }
            } else {
                if (q.getChosenAnswer() == null) {
                    opponentStillAnswering = true;
                }

                if (q.getOpponentsGuess() == null) {
                    stillGuessing = true;
                }
            }
        }

        // I need to answer questions.
        if (stillAnswering) {
            return GAME_STATE_STARTED;
        }

        // I'm done, they need to answer questions.
        if (opponentStillAnswering) {
            return GAME_STATE_WAITING_FOR_OPPONENT;
        }

        // Both of us have answered, now I need to guess theirs.
        if (stillGuessing) {
            return GAME_STATE_GUESSING_OPPONENTS_ANSWERS;
        }

        // I'm completely done, they need to finish guessing.
        if (opponentStillGuessing) {
            return GAME_STATE_WAITING_FOR_OPPONENT;
        }

        // Both of us are completely done.
        return GAME_STATE_FINISHED;

    }

    // How many out of the 5 questions I need to pick answers for I have left
    @NonNull
    public List<QuestionModel> myQuestionsRemaining(GameModel game) {
        return filterQuestions(game, new QuestionFilter() {
            @Override
            public boolean pass(GameModel game, QuestionModel question) {
                return question.getUser().getId().equals(game.getUser().getId())
                        && question.getChosenAnswer() == null;
            }
        });
    }

    // How many out of the 5 questions my opponent needs to pick answers for they have left
    @NonNull
    public List<QuestionModel> opponentsQuestionsRemaining(GameModel game) {
        return filterQuestions(game, new QuestionFilter() {
            @Override
            public boolean pass(GameModel game, QuestionModel question) {
                return !question.getUser().getId().equals(game.getUser().getId())
                        && question.getChosenAnswer() == null;
            }
        });
    }

    // How many questions out of my 5 questions the opponent hasn't guessed yet
    @NonNull
    public List<QuestionModel> myAnswersUnguessed(GameModel game) {
        return filterQuestions(game, new QuestionFilter() {
            @Override
            public boolean pass(GameModel game, QuestionModel question) {
                return question.getUser().getId().equals(game.getUser().getId())
                        && question.getOpponentsGuess() == null;
            }
        });
    }

    // How many questions out of their 5 questions I haven't guessed yet
    @NonNull
    public List<QuestionModel> opponentsAnswersUnguessed(GameModel game) {
        return filterQuestions(game, new QuestionFilter() {
            @Override
            public boolean pass(GameModel game, QuestionModel question) {
                return !question.getUser().getId().equals(game.getUser().getId())
                        && question.getOpponentsGuess() == null;
            }
        });
    }

    @NonNull
    public List<QuestionModel> myQuestions(GameModel game) {
        return filterQuestions(game, new QuestionFilter() {
            @Override
            public boolean pass(GameModel game, QuestionModel question) {
                return question.getUser().getId().equals(game.getUser().getId());
            }
        });
    }

    @NonNull
    public List<QuestionModel> opponentsQuestions(GameModel game) {
        return filterQuestions(game, new QuestionFilter() {
            @Override
            public boolean pass(GameModel game, QuestionModel question) {
                return !question.getUser().getId().equals(game.getUser().getId());
            }
        });
    }

    private interface QuestionFilter {
        boolean pass(GameModel game, QuestionModel question);
    }

    @NonNull
    private List<QuestionModel> filterQuestions(GameModel game, QuestionFilter filter) {
        if (game == null) {
            return ImmutableList.of();
        }

        final List<QuestionModel> questions = new ArrayList<>();

        for (QuestionModel question : game.getQuestions()) {
            if (filter.pass(game, question)) {
                questions.add(question);
            }
        }

        // Make sure questions appear in the same order for both players
        Collections.sort(questions, new Comparator<QuestionModel>() {
            @Override
            public int compare(QuestionModel lhs, QuestionModel rhs) {
                if (lhs.getId().equals(rhs.getId())) {
                    return 0;
                }

                return lhs.getId().hashCode() < rhs.getId().hashCode() ? -1 : 1;
            }
        });

        return questions;

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
        ApiService.use().post("game/" + game.getId() + "/kick-in-the-face", null, new AsyncHttpResponseHandler() {
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

    public void loadGame(String id, final RealmObjectResponseHandler<GameModel> callback) {
        ApiService.use().get("me/game/" + id, null, callback);
    }

    public void answerQuestion(GameModel game, QuestionModel question, int answer) {
        Realm realm = RealmService.use().get();

        realm.beginTransaction();
        question.setChosenAnswer(answer);
        realm.commitTransaction();

        ApiService.use().post("game/" + game.getId() + "/answer/" + question.getId() + "/" + answer, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    public void guessAnswer(GameModel game, QuestionModel question, int guess) {
        Realm realm = RealmService.use().get();

        realm.beginTransaction();
        question.setOpponentsGuess(guess);
        realm.commitTransaction();

        ApiService.use().post("game/" + game.getId() + "/guess/" + question.getId() + "/" + guess, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    @Nullable
    public RealmResults<UserModel> filterFriends(String like) {
        return RealmService.use().get()
                .where(UserModel.class)
                .notEqualTo("id", myUserId())
                .beginGroup()
                    .contains("firstName", like, Case.INSENSITIVE)
                    .or()
                    .contains("lastName", like, Case.INSENSITIVE)
                .endGroup()
                .findAllSorted("firstName", Sort.ASCENDING);
    }

    @Nullable
    public RealmResults<UserModel> getFriends() {
        if (myUserId() == null) {
            Log.w(Config.LOGGER, "Tried to get friends but no user");
            return null;
        }

        ApiService.use().get("me/friends", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody == null || responseBody.length == 0) {

                } else {
                    Realm realm = RealmService.use().get();

                    try {
                        String string = new String(responseBody, "UTF-8");

                        JSONArray jsonArray = new JSONArray(string);

                        realm.beginTransaction();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            realm.createOrUpdateObjectFromJson(UserModel.class, jsonObject.getJSONObject("user"));

                            if (jsonObject.has("game")) {
                                realm.createOrUpdateObjectFromJson(GameModel.class, jsonObject.getJSONObject("game"));
                            }
                        }
                        // They are now added and the UI will automatically update!
                    } catch (JSONException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                        onFailure(-1, null, null, null);
                    } finally {
                        realm.commitTransaction();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (HttpStatus.SC_UNAUTHORIZED == statusCode) {
                    UserService.use().logout();
                    Toast.makeText(GameService.use().context(), "Log in required.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GameService.use().context(), "There was an error. Bummer.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return RealmService.use().get()
                .where(UserModel.class)
                .notEqualTo("id", myUserId())
                .findAllSorted("firstName", Sort.ASCENDING);
    }

    @Nullable
    public GameModel latestGame() {
        return latestGameWith(null);
    }

    @Nullable
    public GameModel latestGameWith(UserModel user) {
        if (myUserId() == null) {
            return null;
        }

        RealmQuery<GameModel> gamesQuery = RealmService.use().get()
                .where(GameModel.class)
                .equalTo("user.id", myUserId());

        if (user != null) {
            gamesQuery.equalTo("opponent.id", user.getId());
        }

        RealmResults<GameModel> games = gamesQuery.findAllSorted("created", Sort.DESCENDING);

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
