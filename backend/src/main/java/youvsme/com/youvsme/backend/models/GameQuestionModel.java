package youvsme.com.youvsme.backend.models;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by jacob on 3/5/16.
 */
@Entity
public class GameQuestionModel extends Model {
    @Index public Ref<UserModel> user;
    @Index public Ref<GameModel> game;
    public Ref<QuestionModel> question;
    public Integer chosenAnswer;
    public Integer opponentsGuess;

    public Ref<UserModel> getUser() {
        return user;
    }

    public void setUser(Ref<UserModel> user) {
        this.user = user;
    }

    public Ref<GameModel> getGame() {
        return game;
    }

    public void setGame(Ref<GameModel> game) {
        this.game = game;
    }

    public Ref<QuestionModel> getQuestion() {
        return question;
    }

    public void setQuestion(Ref<QuestionModel> question) {
        this.question = question;
    }

    public Integer getChosenAnswer() {
        return chosenAnswer;
    }

    public void setChosenAnswer(Integer chosenAnswer) {
        this.chosenAnswer = chosenAnswer;
    }

    public Integer getOpponentsGuess() {
        return opponentsGuess;
    }

    public void setOpponentsGuess(Integer opponentsGuess) {
        this.opponentsGuess = opponentsGuess;
    }
}
