package youvsme.com.youvsme.util;

import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import youvsme.com.youvsme.services.JsonService;

/**
 * Created by jacob on 4/21/16.
 */
public abstract class CorrectCallback extends AsyncHttpResponseHandler {
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        if (responseBody == null) {
            onResponse(false);
            return;
        }

        try {
            JsonObject json = JsonService.get().fromJson(new String(responseBody, "UTF-8"), JsonObject.class);
            onResponse(json.get("correct").getAsBoolean());
        } catch (UnsupportedEncodingException e) {
            onResponse(false);
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        onResponse(false);
    }

    public abstract void onResponse(boolean correct);
}
