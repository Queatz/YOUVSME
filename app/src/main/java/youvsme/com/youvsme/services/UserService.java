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
import com.loopj.android.http.RequestParams;

import java.util.HashSet;
import java.util.Set;

import youvsme.com.youvsme.models.UserModel;
import youvsme.com.youvsme.states.NoUserState;
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

    Runnable callback;
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
                        callback.run();
                        callback = null;
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

                                    playGames();
                                }
                            }

                            @Override
                            public void failure(int statusCode, String response) {
                                callback.run();
                                callback = null;
                                Toast.makeText(GameService.use().context(), "There was an error. Bummer.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancel() {
                        callback.run();
                        callback = null;
                        Log.d("YOUVSME", "Log in canceled by user");
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        callback.run();
                        callback = null;
                        Log.d("YOUVSME", "Log in error: " + exception);
                        // App code
                    }
                });
    }

    private void playGames() {
        StateService.use().go(new SearchForOpponentState());
    }

    public void facebookLogin(Activity activity, Runnable callback) {
        this.callback = callback;
        prepareForFacebookLogin();

        Set<String> permissions = new HashSet<>();
        permissions.add("public_profile");
        permissions.add("user_friends");

        LoginManager.getInstance().logInWithReadPermissions(activity, permissions);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void initialize() {
        prepareForFacebookLogin();
    }

    public void logout() {
        StateService.use().go(new NoUserState());
    }
}
