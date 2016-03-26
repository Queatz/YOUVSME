package youvsme.com.youvsme.services;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import cz.msebera.android.httpclient.HttpStatus;
import youvsme.com.youvsme.RegistrationIntentService;
import youvsme.com.youvsme.models.GameModel;
import youvsme.com.youvsme.states.GameState;
import youvsme.com.youvsme.states.NoUserState;
import youvsme.com.youvsme.states.SearchForOpponentState;
import youvsme.com.youvsme.states.State;
import youvsme.com.youvsme.util.Config;
import youvsme.com.youvsme.util.RealmObjectResponseHandler;

/**
 * Created by jacob on 2/28/16.
 */
public class StateService {

    private static StateService instance;
    private AppCompatActivity activity;

    public static StateService use() {
        if (instance == null) {
            synchronized (StateService.class) {
                if (instance == null) {
                    instance = new StateService();
                }
            }
        }

        return instance;
    }

    private State activeState;

    public void setActivity(final AppCompatActivity activity) {
        this.activity = activity;

        loadInitialState();

        Intent intent = new Intent(activity, RegistrationIntentService.class);
        activity.startService(intent);

        if (GameService.use().currentUser() != null) {
            GameService.use().loadGame(new RealmObjectResponseHandler<GameModel>() {
                @Override
                public void success(GameModel response) {
                    loadInitialState();
                }

                @Override
                public void failure(int statusCode, String response) {
                    if (statusCode == HttpStatus.SC_NOT_FOUND) {
                        return;
                    }

                    Toast.makeText(activity, "Connection to your opponent was severed due to unknown forces in your surroundings.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void loadInitialState() {
        State state = null;

        switch (GameService.use().getState()) {
            case NO_USER:
                state = new NoUserState();
                break;
            case NO_OPPONENT:
                state = new SearchForOpponentState();
                break;
            case IN_GAME:
            case LAST_GAME_FINISHED:
                state = new GameState();
                break;
        }

//        if (StateService.use().getState() != null && state.getClass().equals(StateService.use().getState().getClass())) {
//            return;
//        }

        go(state);
    }

    public void go(@NonNull final State state) {
        activeState = state;
        activeState.show(activity);
    }

    public boolean back() {
        if (activeState == null) {
            return false;
        } else {
            return activeState.back();
        }
    }

    public State getState() {
        return activeState;
    }
}
