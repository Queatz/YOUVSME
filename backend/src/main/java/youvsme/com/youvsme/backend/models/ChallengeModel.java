package youvsme.com.youvsme.backend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

import java.util.List;

/**
 * Created by jacob on 2/25/16.
 */

@Entity
public class ChallengeModel extends Model {
    @Index public boolean active;
    @Index public List<UserModel> users;
    public String wager;
    public String wagerNote;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<UserModel> getUsers() {
        return users;
    }

    public void setUsers(List<UserModel> users) {
        this.users = users;
    }

    public String getWager() {
        return wager;
    }

    public void setWager(String wager) {
        this.wager = wager;
    }

    public String getWagerNote() {
        return wagerNote;
    }

    public void setWagerNote(String wagerNote) {
        this.wagerNote = wagerNote;
    }
}
