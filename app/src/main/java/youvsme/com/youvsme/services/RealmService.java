package youvsme.com.youvsme.services;

import io.realm.Realm;

/**
 * Created by jacob on 2/28/16.
 */
public class RealmService {

    private static RealmService instance;
    public static RealmService use() {
        if (instance == null) {
            synchronized (RealmService.class) {
                if (instance == null) {
                    instance = new RealmService();
                }
            }
        }

        return instance;
    }

    private Realm realm = null;

    public Realm get() {
        if (realm == null || realm.isClosed()) {
            realm = Realm.getInstance(GameService.use().context());
        }

        return realm;
    }
}
