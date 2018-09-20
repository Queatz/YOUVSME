package youvsme.com.youvsme.services;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

/**
 * Created by jacob on 2/28/16.
 */
public class RealmService {

    private static RealmService instance;
    private Realm realm = null;

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

    public Realm get() {
        if (realm == null || realm.isClosed()) {
            Realm.init(GameService.use().context());
            realm = Realm.getInstance(new RealmConfiguration.Builder()
                    .migration(new RealmMigration() {
                        @Override
                        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                            
                        }
                    })
                    .deleteRealmIfMigrationNeeded().build());
        }

        return realm;
    }
}
