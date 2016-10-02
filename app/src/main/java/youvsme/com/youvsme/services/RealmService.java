package youvsme.com.youvsme.services;

import android.os.Looper;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.exceptions.RealmMigrationNeededException;

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
            realm = Realm.getInstance(new RealmConfiguration.Builder(GameService.use().context())
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
