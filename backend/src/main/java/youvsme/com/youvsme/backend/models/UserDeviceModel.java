package youvsme.com.youvsme.backend.models;

import com.googlecode.objectify.annotation.Index;

/**
 * Created by jacob on 3/4/16.
 */
public class UserDeviceModel extends Model {
    @Index public UserModel user;
    @Index public String deviceToken;

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
