package youvsme.com.youvsme.services;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import youvsme.com.youvsme.states.NoUserState;
import youvsme.com.youvsme.states.State;
import youvsme.com.youvsme.util.Config;

/**
 * Created by jacob on 2/28/16.
 */
public class StateService {

    private static StateService instance;
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
        switch (GameService.use().getState()) {
            case NO_USER:
                activeState = new NoUserState();
                break;
            case NO_OPPONENT:
            case ANSWERING_QUESTIONS:
            case WAITING_FOR_OPPONENT:
            case LAST_GAME_FINISHED:
            default:
        }

        if (activeState == null) {
            Log.w(Config.LOGGER, "No state found for active game state. Activity will not be initialized.");
            return;
        }

        activeState.show(activity);
    }

    public boolean back() {
        if (activeState == null) {
            return false;
        } else {
            return activeState.back();
        }
    }
}
