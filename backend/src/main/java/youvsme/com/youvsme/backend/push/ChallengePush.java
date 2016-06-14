package youvsme.com.youvsme.backend.push;

import youvsme.com.youvsme.backend.Config;

/**
 * Created by jacob on 3/5/16.
 */
public class ChallengePush extends Push {
    public String user;
    public boolean isWager;

    public ChallengePush(String game, String user, boolean isWager) {
        super(Config.PUSH_NEW_CHALLENGE, game);
        this.user = user;
        this.isWager = isWager;
    }
}
