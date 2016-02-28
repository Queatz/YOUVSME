package youvsme.com.youvsme.services;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

/**
 * Created by jacob on 2/27/16.
 */
public class ApiService {

    private static ApiService instance;
    public static ApiService use() {
        if (instance == null) {
            synchronized (ApiService.class) {
                if (instance == null) {
                    instance = new ApiService();
                }
            }
        }

        return instance;
    }

    final static String apiBase = "http://you--vs--me.appspot.com/api/";

    AsyncHttpClient client;

    public ApiService() {
        client = new AsyncHttpClient();
    }

    public void get(String path, RequestParams params, ResponseHandlerInterface callback) {
        client.get(apiBase + path, params, callback);
    }
}
