package youvsme.com.youvsme.states;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import youvsme.com.youvsme.R;
import youvsme.com.youvsme.fragments.EntranceFragment;
import youvsme.com.youvsme.fragments.HowItWorksFragment;
import youvsme.com.youvsme.services.UserService;
import youvsme.com.youvsme.util.ViewUtil;

/**
 * Created by jacob on 2/28/16.
 */
public class NoUserState implements State {
    private Fragment entranceFragment = new EntranceFragment();
    private Fragment howItWorksFragment = new HowItWorksFragment();
    private boolean backToEntranceFragment;
    private AppCompatActivity activity;
    private View.OnClickListener clickListener;
    private View facebookButton;

    @Override
    public void show(final AppCompatActivity activity) {
        this.activity = activity;

        activity.setContentView(R.layout.activity_entrance);

        View howItWorksButton = activity.findViewById(R.id.how_it_works_button);

        howItWorksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToEntranceFragment = true;
                showFragment(howItWorksFragment);
            }
        });

        facebookButton = activity.findViewById(R.id.facebookButton);

        UserService.use().initialize();

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookButton.setOnClickListener(null);
                facebookButton.setAlpha(.5f);
                login();
            }
        };

        facebookButton.setOnClickListener(clickListener);

        ViewUtil.battle(howItWorksButton, 1000);
        ViewUtil.battle(facebookButton, 1125);

        showFragment(entranceFragment);
    }

    private void login() {
        UserService.use().facebookLogin(activity, new Runnable() {
            @Override
            public void run() {
                facebookButton.setOnClickListener(clickListener);
                facebookButton.setAlpha(1f);
            }
        });
    }

    @Override
    public boolean back() {
        if (backToEntranceFragment) {
            showFragment(entranceFragment);
            backToEntranceFragment = false;
            return true;
        } else {
            return false;
        }
    }

    private void showFragment(Fragment fragment) {
        activity.findViewById(R.id.how_it_works_button).setVisibility(
                fragment == entranceFragment ? View.VISIBLE : View.GONE);

        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, 0);
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
    }

    @Override
    public void backPressed() {
        if(!back()) {
            activity.onBackPressed();
        }
    }
}