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
import youvsme.com.youvsme.util.ViewUtil;

/**
 * Created by jacob on 2/28/16.
 */
public class WagerSentFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wager_sent, null);

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StateService.use().getState().backPressed();
            }
        });

        view.findViewById(R.id.beginGameButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SearchForOpponentState) StateService.use().getState()).beginGame();
            }
        });

        ViewUtil.battle(view.findViewById(R.id.wagerSentIcon), 0);
        ViewUtil.battle(view.findViewById(R.id.wagerSentText), 150);
        ViewUtil.battle(view.findViewById(R.id.beginGameButton), 250);

        return view;
    }
}
