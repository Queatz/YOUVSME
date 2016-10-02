package youvsme.com.youvsme.util;

import android.app.Service;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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

    public static int px(Context context, int dp) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}
