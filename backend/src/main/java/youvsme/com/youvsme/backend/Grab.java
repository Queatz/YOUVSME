package youvsme.com.youvsme.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jacob on 2/25/16.
 */
public class Grab {
    private static Map<Class, Object> singletons;

    static {
        singletons = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public static <T> T grab(Class<T> clazz) {
        if (!singletons.containsKey(clazz)) {
            try {
                singletons.put(clazz, clazz.newInstance());
            } catch (InstantiationException e) {
                Logger.getAnonymousLogger().log(Level.SEVERE, "Grab can't grab " + clazz.getSimpleName(), e);
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                Logger.getAnonymousLogger().log(Level.SEVERE, "Grab can't grab " + clazz.getSimpleName(), e);
                throw new RuntimeException(e);
            }
        }

        return (T) singletons.get(clazz);
    }
}
