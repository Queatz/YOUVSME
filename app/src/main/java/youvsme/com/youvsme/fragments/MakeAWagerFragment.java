package youvsme.com.youvsme.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.base.Strings;

import youvsme.com.youvsme.R;
import youvsme.com.youvsme.services.StateService;
import youvsme.com.youvsme.states.SearchForOpponentState;
import youvsme.com.youvsme.util.Helpers;

/**
 * Created by jacob on 2/28/16.
 */
public class MakeAWagerFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_make_a_wager, null);

        view.findViewById(R.id.sendWagerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String wagerWhat = ((EditText) view.findViewById(R.id.wagerWhat)).getText().toString();
                final String wagerNote = ((EditText) view.findViewById(R.id.wagerNote)).getText().toString();

                if (Strings.isNullOrEmpty(wagerWhat)) {
                    Toast.makeText(getContext(), getString(R.string.no_wager), Toast.LENGTH_SHORT).show();
                    return;
                }

                ((SearchForOpponentState) StateService.use().getState()).setWager(wagerWhat, wagerNote);
            }
        });

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SearchForOpponentState) StateService.use().getState()).backPressed();
            }
        });

        view.findViewById(R.id.skipWagerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SearchForOpponentState) StateService.use().getState()).skipWager();
            }
        });

        Helpers.keyboard(view.findViewById(R.id.wagerWhat), true);

        return view;
    }
}
