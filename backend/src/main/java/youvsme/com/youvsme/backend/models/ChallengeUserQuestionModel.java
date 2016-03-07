package youvsme.com.youvsme.backend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by jacob on 3/5/16.
 */
@Entity
public class ChallengeUserQuestionModel extends Model {
    @Index public UserModel user;
    @Index public ChallengeModel challenge;
    public QuestionModel question;
    public Integer chosenAnswer;
    public Integer opponentsGuess;

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public ChallengeModel getChallenge() {
        return challenge;
    }

    public void setChallenge(ChallengeModel challenge) {
        this.challenge = challenge;
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
