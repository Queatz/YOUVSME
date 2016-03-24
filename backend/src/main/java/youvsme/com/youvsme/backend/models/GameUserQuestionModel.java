package youvsme.com.youvsme.backend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by jacob on 3/5/16.
 */
@Entity
public class GameUserQuestionModel extends Model {
    @Index public UserModel user;
    @Index public GameModel game;
    public QuestionModel question;
    public Integer chosenAnswer;
    public Integer opponentsGuess;

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public GameModel getGame() {
        return game;
    }

    public void setGame(GameModel game) {
        this.game = game;
    }

    public QuestionModel getQuestion() {
        return question;
    }

    public void setQuestion(QuestionModel question) {
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
