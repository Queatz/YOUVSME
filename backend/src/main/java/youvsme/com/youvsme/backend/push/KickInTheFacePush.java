package youvsme.com.youvsme.backend.push;

import youvsme.com.youvsme.backend.Config;

/**
 * Created by jacob on 3/5/16.
 */
public class KickInTheFacePush extends Push {
    public String user;

    public KickInTheFacePush(String game, String user) {
        super(Config.PUSH_KICK_IN_THE_FACE, game);
        this.user = user;
    }
}
