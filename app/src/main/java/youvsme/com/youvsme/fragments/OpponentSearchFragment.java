package youvsme.com.youvsme.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import youvsme.com.youvsme.R;
import youvsme.com.youvsme.adapters.FriendsAdapter;
import youvsme.com.youvsme.models.UserModel;
import youvsme.com.youvsme.services.GameService;
import youvsme.com.youvsme.services.StateService;
import youvsme.com.youvsme.states.RecentGamesState;
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

        final EditText opponentSearch = (EditText) view.findViewById(R.id.opponentSearch);
        final ListView opponents = (ListView) view.findViewById(R.id.opponentsList);

        opponentSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                RealmResults<UserModel> friends = GameService.use().filterFriends(s.toString());
                opponents.setAdapter(new FriendsAdapter(GameService.use().context(), friends));
            }
        });

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StateService.use().go(new RecentGamesState());
                Helpers.keyboard(opponentSearch, false);
            }
        });

        view.findViewById(R.id.invite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameService.use().invite();
            }
        });

        view.post(new Runnable() {
            @Override
            public void run() {
                Helpers.keyboard(opponentSearch, false);
            }
        });

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        RealmResults<UserModel> friends = GameService.use().getFriends();

        if (friends != null) {
            opponents.setAdapter(new FriendsAdapter(GameService.use().context(), friends));

            friends.addChangeListener(new RealmChangeListener<RealmResults<UserModel>>() {
                @Override
                public void onChange(RealmResults<UserModel> element) {
                    view.findViewById(R.id.invitePartner).setVisibility(element.size() > 0 ? View.INVISIBLE : View.VISIBLE);
                }
            });

            view.findViewById(R.id.invitePartner).setVisibility(friends.size() > 0 ? View.INVISIBLE : View.VISIBLE);
        } else {
            view.findViewById(R.id.invitePartner).setVisibility(View.VISIBLE);

        }


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
