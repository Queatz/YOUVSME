package youvsme.com.youvsme.services;

import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

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

    public void preload() {
        ApiService.use().get("opponents", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Adds all opponents
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // We didn't find any opponents
            }
        });
    }
}
