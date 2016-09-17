package youvsme.com.youvsme.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import io.realm.RealmResults;
import io.realm.Sort;
import youvsme.com.youvsme.R;
import youvsme.com.youvsme.adapters.RecentGamesAdapter;
import youvsme.com.youvsme.models.GameModel;
import youvsme.com.youvsme.services.RealmService;
import youvsme.com.youvsme.services.StateService;

/**
 * Created by jacob on 7/8/16.
 */
public class RecentGamesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recent_games, null);

        ListView recentGamesList = (ListView) view.findViewById(R.id.recentGames);

        RealmResults<GameModel> recentGames = RealmService.use().get().where(GameModel.class)
                .isNotNull("questions.chosenAnswer")
                .isNotNull("questions.opponentsGuess")
                .findAllSorted("created", Sort.DESCENDING);

        recentGamesList.setAdapter(new RecentGamesAdapter(getContext(), recentGames));

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StateService.use().back();
            }
        });

        return view;
    }
}
