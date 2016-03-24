package youvsme.com.youvsme.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by jacob on 3/6/16.
 */

@RealmClass
public class QuestionModel extends RealmObject {
    @PrimaryKey
    private String id;

    public GameModel game;
    public UserModel user;

    /**
     * The text of the question.
     */
    public String questionText;

    /**
     * The options of the question.
     *
     * @value JSON array of questions.
     */
    public String choices;

    /**
     * The chosen answer, by the user.
     */
    public Integer chosenAnswer;

    /**
     * The user's opponent's guess to their chosen answer.
     */
    public Integer opponentsGuess;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GameModel getGame() {
        return game;
    }

    public void setGame(GameModel game) {
        this.game = game;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getChoices() {
        return choices;
    }

    public void setChoices(String choices) {
        this.choices = choices;
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
