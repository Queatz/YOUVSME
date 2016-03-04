package youvsme.com.youvsme.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import io.realm.RealmResults;
import youvsme.com.youvsme.R;
import youvsme.com.youvsme.adapters.FriendsAdapter;
import youvsme.com.youvsme.models.UserModel;
import youvsme.com.youvsme.services.GameService;
import youvsme.com.youvsme.services.StateService;
import youvsme.com.youvsme.states.SearchForOpponentState;
import youvsme.com.youvsme.util.Helpers;

/**
 * Created by jacob on 2/28/16.
 */
public class OpponentSearchFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_opponent_search, null);

        final View opponentSearch = view.findViewById(R.id.opponentSearch);

        opponentSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // filter list
            }
        });

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SearchForOpponentState) StateService.use().getState()).backButton();
            }
        });

        Helpers.keyboard(opponentSearch, true);

        final ListView opponents = (ListView) view.findViewById(R.id.opponentsList);
        RealmResults<UserModel> friends = GameService.use().getFriends();
        opponents.setAdapter(new FriendsAdapter(GameService.use().context(), friends));

        opponents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((SearchForOpponentState) StateService.use().getState()).selectOpponent((UserModel) opponents.getAdapter().getItem(position));
                Helpers.keyboard(opponentSearch, false);
            }
        });

        return view;
    }
}
