package youvsme.com.youvsme.util;

import android.app.Service;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by jacob on 2/28/16.
 */
public class Helpers {

    public static void keyboard(View view, boolean show) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Service.INPUT_METHOD_SERVICE);

        if(show)
            inputMethodManager.showSoftInput(view, 0);
        else
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
