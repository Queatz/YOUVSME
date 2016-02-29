package youvsme.com.youvsme.backend;

import java.util.UUID;

/**
 * Created by jacob on 2/28/16.
 */
public class Util {
    public static String newRandomToken() {
        return UUID.randomUUID().toString() +
                UUID.randomUUID().toString() +
                UUID.randomUUID().toString();
    }
}
