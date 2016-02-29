package youvsme.com.youvsme.util;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmObject;
import youvsme.com.youvsme.services.RealmService;

/**
 * Created by jacob on 2/28/16.
 */
public abstract class RealmObjectResponseHandler<T extends RealmObject> extends AsyncHttpResponseHandler {

    @Override
    @SuppressWarnings("unchecked")
    public final void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        if (responseBody == null || responseBody.length == 0) {
            success(null);
        } else {
            try {
                String string = new String(responseBody, "UTF-8");

                Class<T> clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

                Realm realm = RealmService.use().get();

                realm.beginTransaction();
                T object = realm.createOrUpdateObjectFromJson(clazz, string);
                realm.commitTransaction();

                success(object);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                failure(-1, null);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        if (responseBody == null || responseBody.length == 0) {
            failure(statusCode, null);
        } else {
            try {
                failure(statusCode, new String(responseBody, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                failure(statusCode, null);
            }
        }

        error.printStackTrace();
    }

    public abstract void success(T response);
    public abstract void failure(int statusCode, String response);
}
