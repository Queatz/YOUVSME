package youvsme.com.youvsme.backend.views;

import java.util.List;

import youvsme.com.youvsme.backend.models.GameUserQuestionModel;

/**
 * Created by jacob on 3/24/16.
 */
public class QuestionView {
    public String id;
    public String text;
    public UserView user;
    public Integer choosenAnswer;
    public Integer opponentsGuess;
    public List<String> choices;

    public QuestionView(GameUserQuestionModel question) {
        this.id = question.getId();
        this.text = question.getQuestion().getText();
        this.user = new UserView(question.getUser());
        this.choices = question.getQuestion().getChoices();
        this.choosenAnswer = question.getChosenAnswer();
        this.opponentsGuess = question.getOpponentsGuess();
    }
}
