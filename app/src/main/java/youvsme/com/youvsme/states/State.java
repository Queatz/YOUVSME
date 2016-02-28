package youvsme.com.youvsme.states;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by jacob on 2/28/16.
 */
public interface State {
    /**
     * Show this state inside the activity.
     */
    void show(final AppCompatActivity activity);

    /**
     * Called when the user presses back.
     *
     * @return true if the state handled the back press, or false if the system should handle it.
     */
    boolean back();
}
