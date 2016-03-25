package youvsme.com.youvsme.services;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import youvsme.com.youvsme.states.GameState;
import youvsme.com.youvsme.states.NoUserState;
import youvsme.com.youvsme.states.SearchForOpponentState;
import youvsme.com.youvsme.states.State;
import youvsme.com.youvsme.util.Config;

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
