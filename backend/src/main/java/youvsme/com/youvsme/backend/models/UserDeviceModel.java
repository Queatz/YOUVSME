package youvsme.com.youvsme.backend.models;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by jacob on 3/4/16.
 */
@Entity
public class UserDeviceModel extends Model {
    @Index public Ref<UserModel> user;
    @Index public String deviceToken;

    public Ref<UserModel> getUser() {
        return user;
    }

    public void setUser(Ref<UserModel> user) {
        this.user = user;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
