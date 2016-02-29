package youvsme.com.youvsme.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import youvsme.com.youvsme.R;
import youvsme.com.youvsme.services.StateService;
import youvsme.com.youvsme.states.SearchForOpponentState;
import youvsme.com.youvsme.util.Helpers;

/**
 * Created by jacob on 2/28/16.
 */
public class OpponentSearchFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_opponent_search, null);

        view.findViewById(R.id.opponentSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SearchForOpponentState) StateService.use().getState()).selectOpponent(/*TODO UserModel*/);
            }
        });

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SearchForOpponentState) StateService.use().getState()).backButton();
            }
        });

        view.post(new Runnable() {
            @Override
            public void run() {
                Helpers.keyboard(view, true);
            }
        });

        return view;
    }
}