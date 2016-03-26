package youvsme.com.youvsme;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.JsonObject;

import youvsme.com.youvsme.activities.Entrance;
import youvsme.com.youvsme.services.JsonService;
import youvsme.com.youvsme.services.PushService;
import youvsme.com.youvsme.util.Config;

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
