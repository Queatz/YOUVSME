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
import youvsme.com.youvsme.models.UserModel;

/**
 * Created by jacob on 2/29/16.
 */
public class FriendsAdapter extends RealmBaseAdapter<UserModel> {
    public FriendsAdapter(Context context, RealmResults<UserModel> realmResults) {
        super(context, realmResults, true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_item_friend, null);
        }

        final TextView friendName = (TextView) convertView.findViewById(R.id.name);
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
        Picasso.with(context).load(friend.getPictureUrl()).into(pictureView);

        return convertView;
    }
}
