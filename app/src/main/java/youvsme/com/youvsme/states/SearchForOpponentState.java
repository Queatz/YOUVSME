package youvsme.com.youvsme.states;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import youvsme.com.youvsme.R;
import youvsme.com.youvsme.fragments.BattleThisOpponentFragment;
import youvsme.com.youvsme.fragments.MakeAWagerFragment;
import youvsme.com.youvsme.fragments.OpponentSearchFragment;
import youvsme.com.youvsme.fragments.WagerSentFragment;

/**
 * Created by jacob on 2/28/16.
 */
public class SearchForOpponentState implements State {

    private Fragment currentFragment = null;

    private Fragment opponentSearchFragment = new OpponentSearchFragment();
    private Fragment battleThisOpponentFragment = new BattleThisOpponentFragment();
    private Fragment makeAWagerFragment = new MakeAWagerFragment();
    private Fragment wagerSentFragment = new WagerSentFragment();
    private AppCompatActivity activity;

    @Override
    public void show(AppCompatActivity activity) {
        this.activity = activity;
        activity.setContentView(R.layout.activity_search_for_opponent);

        showFragment(opponentSearchFragment);
    }

    @Override
    public boolean back() {
        if (currentFragment != opponentSearchFragment) {
            showFragment(opponentSearchFragment);
            return true;
        }

        return false;
    }

    private void showFragment(final Fragment fragment) {
        currentFragment = fragment;
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
    }

    public void selectOpponent() {
        showFragment(battleThisOpponentFragment);
    }

    public void confirmBattleOpponent() {
        showFragment(makeAWagerFragment);
    }

    public void setWager(final String what, final String note) {
        showFragment(wagerSentFragment);
    }

    public void skipWager() {
        beginGame();
    }

    public void beginGame() {
        // Go to questions state
    }

    public void backButton() {
        // go back, or close app... (same as back button)
    }
}
