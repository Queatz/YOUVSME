package youvsme.com.youvsme.states;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.google.common.base.Strings;
import com.loopj.android.http.HttpGet;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.HttpStatus;
import youvsme.com.youvsme.R;
import youvsme.com.youvsme.fragments.BattleThisOpponentFragment;
import youvsme.com.youvsme.fragments.MakeAWagerFragment;
import youvsme.com.youvsme.fragments.OpponentSearchFragment;
import youvsme.com.youvsme.fragments.WagerSentFragment;
import youvsme.com.youvsme.models.GameModel;
import youvsme.com.youvsme.models.UserModel;
import youvsme.com.youvsme.services.ApiService;
import youvsme.com.youvsme.services.GameService;
import youvsme.com.youvsme.services.StateService;
import youvsme.com.youvsme.services.UserService;
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

    private UserModel opponent;
    private GameModel game;

    @Override
    public void show(AppCompatActivity activity) {
        this.activity = activity;
        activity.setContentView(R.layout.activity_search_for_opponent);

        // Show splash on first run
        if (StateService.use().getState() == null) {
            final View splashnimation = activity.findViewById(R.id.splashnimation);

            splashnimation.setVisibility(View.VISIBLE);
            splashnimation.animate()
                    .setInterpolator(new AccelerateInterpolator())
                    .alpha(0.f)
                    .scaleX(4.2f)
                    .scaleY(4.2f)
                    .setStartDelay(325)
                    .setDuration(425)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            splashnimation.setVisibility(View.GONE);

                            // Need to check again so that if the app state changes it doesn't steal
                            if (currentFragment == null) {
                                showFragment(opponentSearchFragment);
                            }
                        }
                    })
                    .start();
        } else {
            showFragment(opponentSearchFragment);
        }
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
        transaction.setCustomAnimations(android.R.anim.fade_in, 0);
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
    }

    public void selectOpponent(UserModel opponent) {
        this.opponent = opponent;

        String stakes = GameService.use().inferGameState(GameService.use().latestGameWith(opponent));

        if (stakes == null || GameService.GAME_STATE_FINISHED.equals(stakes)) {
            battleThisOpponentFragment.setOpponent(opponent);
            showFragment(battleThisOpponentFragment);
        } else {
            StateService.use().go(new GameState(GameService.use().latestGameWith(opponent)));
        }
    }

    public void confirmBattleOpponent() {
        showFragment(makeAWagerFragment);
    }

    public void skipWager() {
        setWager(null, null);
    }

    public void setWager(final String what, final String note) {
        if (opponent == null) {
            Log.w(Config.LOGGER, "Can't start a challenge without an opponent");
            return;
        }

        RequestParams params = new RequestParams();
        params.put(Config.PARAM_WAGER_WHAT, what);
        params.put(Config.PARAM_WAGER_NOTE, note);
        params.put(Config.PARAM_OPPONENT, opponent.getId());

        ApiService.use().post("game", params, new RealmObjectResponseHandler<GameModel>() {
            @Override
            public void success(GameModel game) {
                SearchForOpponentState.this.game = game;

                if (Strings.isNullOrEmpty(what)) {
                    beginGame();
                } else {
                    showFragment(wagerSentFragment);
                }
            }

            @Override
            public void failure(int statusCode, String response) {
                if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                    UserService.use().logout();
                }

                Log.w(Config.LOGGER, "Could not send wager");
            }
        });
    }

    public void beginGame() {
        if (game == null) {
            return;
        }

        GameState gameState = new GameState(game);
        gameState.itBegins();
        StateService.use().go(gameState);
    }

    @Override
    public void backPressed() {
        if(!back()) {
            activity.onBackPressed();
        }
    }
}
