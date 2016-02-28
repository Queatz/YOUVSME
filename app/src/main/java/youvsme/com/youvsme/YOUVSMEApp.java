package youvsme.com.youvsme;

import android.app.Application;

import youvsme.com.youvsme.services.GameService;
import youvsme.com.youvsme.services.RealmService;

/**
 * Created by jacob on 2/28/16.
 */
public class YOUVSMEApp extends Application {
    public YOUVSMEApp() {
        GameService.getInstance().initialize(this);
    }
}
