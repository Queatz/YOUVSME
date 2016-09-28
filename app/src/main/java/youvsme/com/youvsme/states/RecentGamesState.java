package youvsme.com.youvsme.states;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import youvsme.com.youvsme.R;
import youvsme.com.youvsme.fragments.RecentGamesFragment;
import youvsme.com.youvsme.services.StateService;

/**
 * Created by jacob on 7/8/16.
 */
public class RecentGamesState implements State {

    private Fragment recentGamesFragment = new RecentGamesFragment();
    private AppCompatActivity activity;

    @Override
    public void show(final AppCompatActivity activity) {
        this.activity = activity;

        activity.setContentView(R.layout.activity_search_for_opponent);

        showFragment(recentGamesFragment);
    }

    @Override
    public boolean back() {
        StateService.use().go(new SearchForOpponentState());
        return true;
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, 0);
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
    }

    @Override
    public void backPressed() {
        if(!back()) {
            StateService.use().back();
        }
    }
}
