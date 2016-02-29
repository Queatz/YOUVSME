package youvsme.com.youvsme.services;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.common.collect.ImmutableSet;
import com.loopj.android.http.RequestParams;

import youvsme.com.youvsme.models.UserModel;
import youvsme.com.youvsme.states.SearchForOpponentState;
import youvsme.com.youvsme.util.Config;
import youvsme.com.youvsme.util.RealmObjectResponseHandler;

/**
 * Created by jacob on 2/23/16.
 */
public class UserService {

    private static UserService instance;
    public static UserService use() {
        if (instance == null) {
            synchronized (UserService.class) {
                if (instance == null) {
                    instance = new UserService();
                }
            }
        }

        return instance;
    }

    CallbackManager facebookCallbackManager = null;

    private void prepareForFacebookLogin() {
        if (facebookCallbackManager != null) {
            return;
        }

        FacebookSdk.sdkInitialize(GameService.use().context());
        facebookCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(facebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d(Config.LOGGER, "Logged in: " + loginResult.getAccessToken());

                        RequestParams params = new RequestParams();
                        params.add(Config.PARAM_FACEBOOK_TOKEN, loginResult.getAccessToken().getToken());

                        ApiService.use().get("me", params, new RealmObjectResponseHandler<UserModel>() {
                            @Override
                            public void success(UserModel user) {
                                if (user != null) {
                                    GameService.use()
                                            .setMyUserId(user.getId())
                                            .setMyUserToken(user.getToken());

                                    StateService.use().go(new SearchForOpponentState());
                                }
                            }

                            @Override
                            public void failure(int statusCode, String response) {
                                Toast.makeText(GameService.use().context(), "There was an error. Bummer.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancel() {
                        Log.d("YOUVSME", "Log in canceled by user");
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d("YOUVSME", "Log in error: " + exception);
                        // App code
                    }
                });
    }

    public void facebookLogin(Activity activity) {
        prepareForFacebookLogin();

        LoginManager.getInstance().logInWithReadPermissions(activity,
                ImmutableSet.of("public_profile", "user_friends"));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void initialize() {
        prepareForFacebookLogin();
    }
}
