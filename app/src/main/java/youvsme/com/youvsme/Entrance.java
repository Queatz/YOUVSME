package youvsme.com.youvsme;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.common.collect.ImmutableSet;

import youvsme.com.youvsme.fragments.EntranceFragment;
import youvsme.com.youvsme.fragments.HowItWorksFragment;
import youvsme.com.youvsme.services.UserService;

public class Entrance extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Fragment entranceFragment = new EntranceFragment();
    private Fragment howItWorksFragment = new HowItWorksFragment();
    private boolean backToEntranceFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserService.getInstance().initialize(getApplicationContext());
        //BattleSearchService.getInstance().preload();

        setContentView(R.layout.activity_entrance);

        View howItWorksButton = findViewById(R.id.how_it_works_button);

        howItWorksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToEntranceFragment = true;
                showFragment(howItWorksFragment);
            }
        });

        View facebookButton = findViewById(R.id.facebook_button);

        CallbackManager callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d("YOUVSME", "Logged in: " + loginResult.getAccessToken());
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

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(Entrance.this,
                        ImmutableSet.of("public_profile", "user_friends"));
            }
        });

        showFragment(entranceFragment);
    }

    private void showFragment(Fragment fragment) {
        findViewById(R.id.how_it_works_button).setVisibility(
                fragment == entranceFragment ? View.VISIBLE : View.GONE);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (backToEntranceFragment) {
            showFragment(entranceFragment);
            backToEntranceFragment = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.entrance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return true;
    }
}
