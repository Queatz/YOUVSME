package youvsme.com.youvsme;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.JsonObject;

import youvsme.com.youvsme.services.JsonService;
import youvsme.com.youvsme.services.PushService;

/**
 * Created by jacob on 3/4/16.
 */
public class MyGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        JsonObject push = JsonService.json(data.getString("data"), JsonObject.class);

        PushService.use().notify(push);
    }
}
