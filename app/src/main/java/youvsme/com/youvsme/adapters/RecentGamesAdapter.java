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
import youvsme.com.youvsme.services.GameService;

/**
 * Created by jacob on 7/8/16.
 */
public class RecentGamesAdapter extends RealmBaseAdapter<GameModel> {
    public RecentGamesAdapter(Context context, RealmResults<GameModel> realmResults) {
        super(context, realmResults);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_item_recent_game, null);
        }

        GameModel game = getItem(position);

        TextView wagerWhatView = (TextView) convertView.findViewById(R.id.wagerWhat);
        TextView wagerNoteView = (TextView) convertView.findViewById(R.id.wagerNote);

        boolean wagerPresent = false;

        if (game.getWager() != null) {
            wagerWhatView.setText(game.getWager());
            wagerWhatView.setVisibility(View.VISIBLE);
            wagerPresent = true;
        } else {
            wagerWhatView.setVisibility(View.GONE);
        }

        if (game.getWagerNote() != null) {
            wagerNoteView.setText(game.getWagerNote());
            wagerNoteView.setVisibility(View.VISIBLE);
            wagerPresent = true;
        } else {
            wagerNoteView.setVisibility(View.GONE);
        }

        convertView.findViewById(R.id.wagerNone).setVisibility(wagerPresent ? View.GONE : View.VISIBLE);

        ImageView opponentPicture = (ImageView) convertView.findViewById(R.id.opponentPicture);
        Picasso.with(context).load(game.getOpponent().getPictureUrl()).into(opponentPicture);

        int usersCorrect = GameService.use().numberOfMyGuessesCorrect(game);
        int opponentsCorrect = GameService.use().numberOfOpponentsGuessesCorrect(game);

        ((TextView) convertView.findViewById(R.id.opponentsFinalScore)).setText("0" + opponentsCorrect);
        ((TextView) convertView.findViewById(R.id.yourFinalScore)).setText("0" + usersCorrect);

        ((TextView) convertView.findViewById(R.id.opponentsName)).setText(game.getOpponent().getFirstName());
        ((TextView) convertView.findViewById(R.id.yourName)).setText(game.getUser().getFirstName());

        return convertView;
    }
}
