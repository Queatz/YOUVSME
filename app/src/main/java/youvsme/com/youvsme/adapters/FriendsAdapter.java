package youvsme.com.youvsme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        TextView friendName = (TextView) convertView.findViewById(R.id.friendName);

        UserModel friend = getItem(position);

        friendName.setText(context.getString(R.string.fullname, friend.getFirstName(), friend.getLastName()));

        return convertView;
    }
}
