package youvsme.com.youvsme.backend.services;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;

import java.util.Date;
import java.util.Random;

import youvsme.com.youvsme.backend.models.GameModel;
import youvsme.com.youvsme.backend.models.GameQuestionModel;
import youvsme.com.youvsme.backend.models.Model;
import youvsme.com.youvsme.backend.models.QuestionModel;
import youvsme.com.youvsme.backend.models.UserDeviceModel;
import youvsme.com.youvsme.backend.models.UserModel;

/**
 * Created by jacob on 2/25/16.
 */

public class ModelService {

    static {
        ObjectifyService.register(GameModel.class);
        ObjectifyService.register(UserModel.class);
        ObjectifyService.register(QuestionModel.class);
        ObjectifyService.register(UserDeviceModel.class);
        ObjectifyService.register(GameQuestionModel.class);
    }

    public static <T extends Model> T create(Class<T> type) {
        try {
            T thing = type.newInstance();
            thing.id = newId();
            thing.created = new Date();
            return thing;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Error instantiating new object of type " + type.getSimpleName());
    }

    public static String newId() {
        return Long.toString(new Random().nextLong());
    }

    public static <T> String id(Key<T> key) {
        return key.getName();
    }

    public static <T> LoadType<T> get(Class<T> clazz) {
        return ofy().load().type(clazz);
    }

    public static <T> Query<T> get(Class<T> clazz, com.google.appengine.api.datastore.Query.Filter filter) {
        return ofy().load().type(clazz).filter(filter);
    }

    public static <T> T get(Key<T> key) {
        return ofy().load().key(key).now();
    }

    public static <T> T get(Class<T> clazz, String id) {
        return ofy().load().type(clazz).id(id).now();
    }

    public static <T> boolean save(T thing) {
        if (Model.class.isAssignableFrom(thing.getClass())) {
            if (((Model) thing).id == null) {
                ((Model) thing).id = newId();
                ((Model) thing).created = new Date();
            }
        }

        return ofy().save().entity(thing).now() != null;

    }

    public static <T> void delete(T thing) {
        ofy().delete().entity(thing).now();
    }

    public static <T> void delete(Class<T> type, String id) {
        ofy().delete().type(type).id(id).now();
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }

    public static <T> Key<T> key(T thing) {
        return Key.create(thing);
    }

    public static <T> Key<T> key(Class<T> type, String id) {
        return Key.create(type, id);
    }

    public static <T> Ref<T> ref(T object) {
        return Ref.create(object);
    }
}
