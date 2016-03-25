package youvsme.com.youvsme.backend.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by jacob on 2/25/16.
 */
public class JsonService {
    private static Gson gson;

    static {
        JsonDeserializer<Date> dateJsonDeserializer = new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        };

        JsonSerializer<Date> dateJsonSerializer = new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(Date date, Type typeOfT, JsonSerializationContext context) throws JsonParseException {
                return new JsonPrimitive(Long.toString(date.getTime()));
            }
        };

        gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, dateJsonDeserializer)
                .registerTypeAdapter(Date.class, dateJsonSerializer)
                .create();
    }

    public String json(Object object) {
        return gson.toJson(object);
    }
}
