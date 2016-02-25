package youvsme.com.youvsme.services;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.common.collect.ImmutableSet;

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

    CallbackManager callbackManager;

    public void initialize(Context context) {
        FacebookSdk.sdkInitialize(context);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d("YOUVSME", "Logged in: " + loginResult.getAccessToken());

//                         TODO: send to server Facebook token
//                        Api.get("/me", {facebookToken: loginResult.getAccessToken().getToken()});

//                         Once logged in  on our sever we can preload users
//                        BattleSearchService.getInstance().preload();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }

    public static void facebookLogin(Activity activity) {
        LoginManager.getInstance().logInWithReadPermissions(activity,
                ImmutableSet.of("public_profile", "user_friends"));
    }
}
