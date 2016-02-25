package youvsme.com.youvsme.services;

import android.content.Context;

import com.facebook.FacebookSdk;

/**
 * Created by jacob on 2/23/16.
 */
public class UserService {

    private static UserService instance;
    public static UserService getInstance() {
        if (instance == null) {
            synchronized (UserService.class) {
                if (instance == null) {
                    instance = new UserService();
                }
            }
        }

        return instance;
    }

    public void initialize(Context context) {
        FacebookSdk.sdkInitialize(context);
    }
}
