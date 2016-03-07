package youvsme.com.youvsme.backend.push;

import youvsme.com.youvsme.backend.Config;

/**
 * Created by jacob on 3/5/16.
 */
public class FinishedAnsweringPush extends Push {
    public String user;

    public FinishedAnsweringPush(String user) {
        super(Config.PUSH_FINISHED_ANSWERING);
        this.user = user;
    }
}
