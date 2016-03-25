package youvsme.com.youvsme.models;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by jacob on 2/28/16.
 */

@RealmClass
public class GameModel extends RealmObject {

    @PrimaryKey
    private String id;
    private String wager;
    private String wagerNote;
    private UserModel user;
    private UserModel opponent;
    private RealmList<QuestionModel> questions;
    private Date created;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public RealmList<QuestionModel> getQuestions() {
        return questions;
    }

    public void setQuestions(RealmList<QuestionModel> questions) {
        this.questions = questions;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
