package youvsme.com.youvsme.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by jacob on 2/28/16.
 */

@RealmClass
public class GameModel extends RealmObject {
    public static final String GAME_STATE_STARTED = "started";
    public static final String GAME_STATE_WAITING_FOR_OPPONENT = "waiting";
    public static final String GAME_STATE_GUESSING_OPPONENTS_ANSWERS = "answering";
    public static final String GAME_STATE_FINISHED = "finished";

    @PrimaryKey
    private String id;

    private UserModel user;
    private UserModel opponent;

    @Index
    private String state;

    @Index
    private Date started;
    private Date finished;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public UserModel getOpponent() {
        return opponent;
    }

    public void setOpponent(UserModel opponent) {
        this.opponent = opponent;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public Date getFinished() {
        return finished;
    }

    public void setFinished(Date finished) {
        this.finished = finished;
    }
}
