package youvsme.com.youvsme.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.text.DateFormat;

/**
 * Created by jacob on 2/28/16.
 */
public class JsonService {
    private static Gson gson;

    static {
        gson = new GsonBuilder().setDateFormat(DateFormat.LONG, DateFormat.LONG).create();
    }

    public static String json(Object object) {
        return gson.toJson(object);
    }

    public static <T> T json(String string, Class<T> clazz) {
        return gson.fromJson(string, clazz);
    }

    public static <T> T json(String string, Type type) {
        return gson.fromJson(string, type);
    }

    public static Gson get() {
        return gson;
    }
}
