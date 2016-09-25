package youvsme.com.youvsme;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.loopj.android.http.RequestParams;

import java.io.IOException;

import youvsme.com.youvsme.services.ApiService;
import youvsme.com.youvsme.util.Config;

/**
 * Created by jacob on 3/4/16.
 */
public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegistrationIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    public void onHandleIntent(Intent intent) {
        InstanceID instanceID = InstanceID.getInstance(this);

        String token;

        try {
            token = instanceID.getToken(Config.GCM_SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Register device for notification delivery
        RequestParams params = new RequestParams();
        params.put(Config.PARAM_DEVICE_TOKEN, token);
        ApiService.use().post("me/device", params, null);
    }
}
