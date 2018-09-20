package youvsme.com.youvsme.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import youvsme.com.youvsme.R;
import youvsme.com.youvsme.services.StateService;
import youvsme.com.youvsme.states.SearchForOpponentState;

import static com.facebook.internal.Utility.isNullOrEmpty;

/**
 * Created by jacob on 2/28/16.
 */
public class MakeAWagerFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_make_a_wager, null);

        final EditText wagerWhatView = (EditText) view.findViewById(R.id.wagerWhat);
        final EditText wagerNoteView = (EditText) view.findViewById(R.id.wagerNote);

        view.findViewById(R.id.sendWagerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String wagerWhat = wagerWhatView.getText().toString();
                final String wagerNote = wagerNoteView.getText().toString();

                if (isNullOrEmpty(wagerWhat)) {
                    Toast.makeText(getContext(), getString(R.string.no_wager), Toast.LENGTH_SHORT).show();
                    return;
                }

                disable();

                ((SearchForOpponentState) StateService.use().getState()).setWager(wagerWhat, wagerNote);
            }
        });

        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                boolean hide = bottom - top < right - left;

                view.findViewById(R.id.skipWagerButton).setVisibility(
                        hide ? View.GONE : View.VISIBLE
                );

                view.findViewById(R.id.bottomLayout).requestLayout();
            }
        });

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StateService.use().getState().backPressed();
            }
        });

        view.findViewById(R.id.skipWagerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disable();
                ((SearchForOpponentState) StateService.use().getState()).skipWager();
            }
        });

        return view;
    }

    private void disable() {
        if (getView() == null) {
            return;
        }

        getView().findViewById(R.id.sendWagerButton).setOnClickListener(null);
        getView().findViewById(R.id.skipWagerButton).setOnClickListener(null);

        getView().findViewById(R.id.sendWagerButton).setAlpha(.5f);
        getView().findViewById(R.id.skipWagerButton).setAlpha(.5f);
    }
}
