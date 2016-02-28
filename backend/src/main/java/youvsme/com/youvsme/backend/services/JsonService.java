package youvsme.com.youvsme.backend.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;

/**
 * Created by jacob on 2/25/16.
 */
public class JsonService {
    private static Gson gson;

    static {
        gson = new GsonBuilder().setDateFormat(DateFormat.LONG, DateFormat.LONG).create();
    }


    public String json(Object object) {
        return gson.toJson(object);
    }
}
