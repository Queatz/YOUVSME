package youvsme.com.youvsme.backend.models;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

import java.util.List;

/**
 * Created by jacob on 2/25/16.
 */

@Entity
public class GameModel extends Model {
    @Index public List<Ref<UserModel>> users;
    public String wager;
    public String wagerNote;

    public List<Ref<UserModel>> getUsers() {
        return users;
    }

    public void setUsers(List<Ref<UserModel>> users) {
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
