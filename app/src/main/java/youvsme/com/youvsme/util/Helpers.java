package youvsme.com.youvsme.util;

import android.app.Service;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import youvsme.com.youvsme.R;

/**
 * Created by jacob on 2/28/16.
 */
public class Helpers {

    public static void keyboard(final View view, final boolean show) {
        view.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Service.INPUT_METHOD_SERVICE);

                if(show) {
                    view.requestFocus();
                    inputMethodManager.showSoftInput(view, 0);
                } else {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

    }
}
