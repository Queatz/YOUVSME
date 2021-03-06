package youvsme.com.youvsme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import youvsme.com.youvsme.R;
import youvsme.com.youvsme.models.GameModel;
import youvsme.com.youvsme.models.UserModel;
import youvsme.com.youvsme.services.GameService;

/**
 * Created by jacob on 2/29/16.
 */
public class FriendsAdapter extends RealmBaseAdapter<UserModel> {

    private final Context context;

    public FriendsAdapter(Context context, RealmResults<UserModel> realmResults) {
        super(realmResults);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_item_friend, null);
        }

        final TextView friendName = (TextView) convertView.findViewById(R.id.name);
        final View action = convertView.findViewById(R.id.action);
        final TextView stakes = (TextView) convertView.findViewById(R.id.stakes);
        final TextView letterView = (TextView) convertView.findViewById(R.id.letter);
        final ImageView pictureView = (ImageView) convertView.findViewById(R.id.picture);

        final UserModel friend = getItem(position);
        final String letter = friend.getFirstName() == null ? "" : friend.getFirstName().substring(0, 1).toUpperCase();

        if (position == 0 || !letter.equals(getItem(position - 1).getFirstName().substring(0, 1).toUpperCase())) {
            letterView.setVisibility(View.VISIBLE);
            letterView.setText(letter);
        } else {
            letterView.setVisibility(View.INVISIBLE);
        }

        friendName.setText(context.getString(R.string.fullname, friend.getFirstName(), friend.getLastName()));
        Picasso.get().load(friend.getPictureUrl()).into(pictureView);

        int stakesStringId = 0;
        int stakesColorId = 0;

        GameModel game = GameService.use().latestGameWith(friend);

        if (game == null) {
            stakesStringId = R.string.no_games_played;
        } else {
            switch (GameService.use().inferGameState(game)) {
                case GameService.GAME_STATE_FINISHED:
                    int mine = GameService.use().numberOfMyGuessesCorrect(game);
                    int thiers = GameService.use().numberOfOpponentsGuessesCorrect(game);

                    if (mine > thiers) {
                        stakesStringId = R.string.last_game_won;
                    } else if (thiers > mine) {
                        stakesStringId = R.string.last_game_lost;
                    } else {
                        stakesStringId = R.string.last_game_tied;
                    }

                    stakesColorId = 0;
                    break;
                case GameService.GAME_STATE_GUESSING_OPPONENTS_ANSWERS:
                case GameService.GAME_STATE_STARTED:
                    stakesStringId = R.string.waiting_on_you;
                    stakesColorId = R.drawable.action_yes;
                    break;
                case GameService.GAME_STATE_WAITING_FOR_OPPONENT:
                    stakesStringId = R.string.waiting_on_them;
                    stakesColorId = R.drawable.action_no;
                    break;
            }
        }

        if (stakesStringId != 0) {
            stakes.setText(context.getString(stakesStringId));
        }

        if (stakesColorId != 0) {
            action.setBackgroundResource(stakesColorId);
        } else {
            action.setBackground(null);
        }

        return convertView;
    }
}
