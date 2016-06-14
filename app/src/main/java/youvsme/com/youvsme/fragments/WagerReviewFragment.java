package youvsme.com.youvsme.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import youvsme.com.youvsme.R;
import youvsme.com.youvsme.models.GameModel;
import youvsme.com.youvsme.services.GameService;
import youvsme.com.youvsme.services.StateService;
import youvsme.com.youvsme.states.GameState;

/**
 * Created by jacob on 3/5/16.
 */
public class WagerReviewFragment extends GameStateFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_wager_review, null);

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StateService.use().getState().backPressed();
            }
        });

        view.findViewById(R.id.acceptChallengeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GameState) StateService.use().getState()).letsGo();
            }
        });

        view.findViewById(R.id.acceptDefeatButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // XXX notify opponnet

                StateService.use().getState().backPressed();
            }
        });

        update(view);

        return view;
    }

    @Override
    public void update() {
        if (getView() != null) {
            update(getView());
        }
    }

    private void update(View view) {
        final GameModel game = ((GameState) StateService.use().getState()).getGame();

        if (view == null || game == null) {
            return;
        }

        ((TextView) view.findViewById(R.id.wagerWhat)).setText(game.getWager());
        ((TextView) view.findViewById(R.id.wagerNote)).setText(game.getWagerNote());
    }
}
