package youvsme.com.youvsme.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import youvsme.com.youvsme.R;
import youvsme.com.youvsme.services.StateService;
import youvsme.com.youvsme.states.GameState;

/**
 * Created by jacob on 3/5/16.
 */
public class SeeWhoWonFragment extends GameStateFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_see_who_won, null);

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StateService.use().getState().backPressed();
            }
        });

        view.findViewById(R.id.seeWhoWonButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GameState) StateService.use().getState()).seeWhoWon();
            }
        });

        return view;
    }

    @Override
    public void update() {

    }
}
