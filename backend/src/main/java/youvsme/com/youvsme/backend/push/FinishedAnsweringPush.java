package youvsme.com.youvsme.backend.push;

import youvsme.com.youvsme.backend.Config;

/**
 * Created by jacob on 3/5/16.
 */
public class FinishedAnsweringPush extends Push {
    public String user;
    public boolean isComplete;

    public FinishedAnsweringPush(String game, String user, boolean isComplete) {
        super(Config.PUSH_FINISHED_ANSWERING, game);
        this.user = user;
        this.isComplete = isComplete;
    }
}
