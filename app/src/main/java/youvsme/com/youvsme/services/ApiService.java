package youvsme.com.youvsme.services;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import cz.msebera.android.httpclient.Header;
import youvsme.com.youvsme.util.Config;

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

    private static ResponseHandlerInterface doNothingHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

        }
    };

    public ApiService() {
        client = new AsyncHttpClient();
    }

    public void get(String path, RequestParams params, ResponseHandlerInterface callback) {
        if (callback == null) {
            callback = doNothingHandler;
        }

        params = injectToken(params);
        client.get(apiBase + path, params, callback);
    }

    public void post(String path, RequestParams params, ResponseHandlerInterface callback) {
        if (callback == null) {
            callback = doNothingHandler;
        }

        params = injectToken(params);
        client.post(apiBase + path, params, callback);
    }

    private RequestParams injectToken(RequestParams params) {
        if (params == null) {
            params = new RequestParams();
        }

        if (params.has(Config.PARAM_TOKEN) || params.has(Config.PARAM_FACEBOOK_TOKEN)) {
            return params;
        }

        params.put(Config.PARAM_TOKEN, GameService.use().myUserToken());

        return params;
    }
}
