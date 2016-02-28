package youvsme.com.youvsme.services;

import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import youvsme.com.youvsme.models.Opponent;

/**
 * Created by jacob on 2/23/16.
 */
public class BattleSearchService {

    private static BattleSearchService instance;
    public static BattleSearchService use() {
        if (instance == null) {
            synchronized (BattleSearchService.class) {
                if (instance == null) {
                    instance = new BattleSearchService();
                }
            }
        }

        return instance;
    }

    List<Opponent> opponents;

    public void preload() {


        // TODO move to server
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d("YOUVSME", "Got opponents: " + response.getRawResponse());

                        opponents = new ArrayList<>();
                        JSONArray json = response.getJSONArray();

                        for (int i = 0; i < json.length(); i++) {
                            try {
                                JSONObject user = json.getJSONObject(i);
                                opponents.add(new Opponent(
                                        user.getString("id"),
                                        user.getString("first_name"),
                                        user.getString("last_name")
                                ));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ).executeAsync();
    }
}
