package youvsme.com.youvsme.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import youvsme.com.youvsme.R;
import youvsme.com.youvsme.models.UserModel;
import youvsme.com.youvsme.services.StateService;
import youvsme.com.youvsme.states.SearchForOpponentState;

/**
 * Created by jacob on 2/28/16.
 */
public class BattleThisOpponentFragment extends Fragment {
    private UserModel opponent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_battle_this_opponent, null);

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SearchForOpponentState) StateService.use().getState()).backPressed();
            }
        });

        view.findViewById(R.id.battleButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SearchForOpponentState) StateService.use().getState()).confirmBattleOpponent();
            }
        });

        decorateView(view);

        return view;
    }

    public void setOpponent(UserModel opponent) {
        this.opponent = opponent;

        if (getView() != null) {
            decorateView(getView());
        }
    }

    private void decorateView(View view) {
        if (opponent == null) {
            return;
        }

        TextView opponentName = (TextView) view.findViewById(R.id.opponentName);
        opponentName.setText(getString(R.string.fullname, opponent.getFirstName(), opponent.getLastName()));

        RoundedImageView opponentPicture = (RoundedImageView) view.findViewById(R.id.opponentPicture);
        Picasso.with(getContext()).load(opponent.getPictureUrl()).into(opponentPicture);
        // TODO when tapping on the opponent picture, anticipate slow-motion explosion thereof
    }
}
