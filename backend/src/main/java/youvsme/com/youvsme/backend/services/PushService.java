package youvsme.com.youvsme.backend.services;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import youvsme.com.youvsme.backend.Config;
import youvsme.com.youvsme.backend.Grab;
import youvsme.com.youvsme.backend.models.UserDeviceModel;
import youvsme.com.youvsme.backend.models.UserModel;

/**
 * Created by jacob on 3/4/16.
 */
public class PushService {

    public void send(UserModel toUser, Object message) {

        Sender sender = new Sender(Config.GCM_API_KEY);
        Message msg = new Message.Builder().addData("data", Grab.grab(JsonService.class).json(message)).build();

        List<UserDeviceModel> userDevices = ModelService.get(UserDeviceModel.class).filter("user", toUser).list();
        for (UserDeviceModel device : userDevices) {
            try {
                Result result = sender.send(msg, device.getDeviceToken(), 5);

                if (result.getMessageId() != null) {
                    String canonicalRegId = result.getCanonicalRegistrationId();
                    if (canonicalRegId != null) {
                        device.setDeviceToken(canonicalRegId);
                        ModelService.save(device);
                    }
                } else {
                    String error = result.getErrorCodeName();
                    if (error.equals(Constants.ERROR_NOT_REGISTERED) || error.equals(Constants.ERROR_MISMATCH_SENDER_ID)) {
                        ModelService.delete(device);
                    }
                }
            } catch (IOException e) {
                Logger.getLogger("push").warning("error sending " + e);
                e.printStackTrace();
            }
        }
    }
}
