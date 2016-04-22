package youvsme.com.youvsme.services;

/**
 * Created by jacob on 2/23/16.
 */
public class BattleSearchService {

    private static BattleSearchService instance;
    public static BattleSearchService use() {
        if (instance == null) {
            synchronized (BattleSearchService.class) {
                if (instance == null) {
                    instance = new BattleSearchService();
                }
            }
        }

        return instance;
    }
}
