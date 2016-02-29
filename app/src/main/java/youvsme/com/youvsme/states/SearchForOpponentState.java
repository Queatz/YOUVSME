package youvsme.com.youvsme.states;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.loopj.android.http.RequestParams;

import youvsme.com.youvsme.R;
import youvsme.com.youvsme.fragments.BattleThisOpponentFragment;
import youvsme.com.youvsme.fragments.MakeAWagerFragment;
import youvsme.com.youvsme.fragments.OpponentSearchFragment;
import youvsme.com.youvsme.fragments.WagerSentFragment;
import youvsme.com.youvsme.models.GameModel;
import youvsme.com.youvsme.models.UserModel;
import youvsme.com.youvsme.services.ApiService;
import youvsme.com.youvsme.util.Config;
import youvsme.com.youvsme.util.RealmObjectResponseHandler;

/**
 * Created by jacob on 2/28/16.
 */
public class SearchForOpponentState implements State {

    private Fragment currentFragment = null;

    private OpponentSearchFragment opponentSearchFragment = new OpponentSearchFragment();
    private BattleThisOpponentFragment battleThisOpponentFragment = new BattleThisOpponentFragment();
    private MakeAWagerFragment makeAWagerFragment = new MakeAWagerFragment();
    private WagerSentFragment wagerSentFragment = new WagerSentFragment();
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

    public void selectOpponent(UserModel opponent) {
        battleThisOpponentFragment.setOpponent(opponent);
        showFragment(battleThisOpponentFragment);
    }

    public void confirmBattleOpponent() {
        showFragment(makeAWagerFragment);
    }

    public void skipWager() {
        setWager(null, null);
    }

    public void setWager(final String what, final String note) {
        RequestParams params = new RequestParams();
        params.put(Config.PARAM_WAGER_WHAT, what);
        params.put(Config.PARAM_WAGER_NOTE, note);

        ApiService.use().post("challenge", params, new RealmObjectResponseHandler<GameModel>() {
            @Override
            public void success(GameModel response) {
                showFragment(wagerSentFragment);
            }

            @Override
            public void failure(int statusCode, String response) {
                Log.w(Config.LOGGER, "Could not send wager");
            }
        });
    }

    public void beginGame() {
        // Go to questions state
    }

    public void backButton() {
        if(!back()) {
            // Close app
        }
    }
}
