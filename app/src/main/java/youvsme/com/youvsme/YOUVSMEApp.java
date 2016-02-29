package youvsme.com.youvsme;

import android.app.Application;

import youvsme.com.youvsme.services.GameService;

/**
 * Created by jacob on 2/28/16.
 */
public class YOUVSMEApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        GameService.use().initialize(getApplicationContext());
    }
}
