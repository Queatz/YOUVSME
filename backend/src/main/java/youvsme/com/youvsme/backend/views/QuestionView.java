package youvsme.com.youvsme.backend.views;

import youvsme.com.youvsme.backend.Grab;
import youvsme.com.youvsme.backend.models.GameQuestionModel;
import youvsme.com.youvsme.backend.services.JsonService;

/**
 * Created by jacob on 3/24/16.
 */
public class QuestionView {
    public String id;
    public String text;
    public UserView user;

    // TODO | This is, for now, a String rather than a List<String>
    // TODO | because Realm does not support List<String> yet.
    public String choices;
    public Integer chosenAnswer;
    public Integer opponentsGuess;

    public QuestionView(GameQuestionModel question) {
        this.id = question.getId();
        this.text = question.getQuestion().getText();
        this.user = new UserView(question.getUser());
        this.choices = Grab.grab(JsonService.class).json(question.getQuestion().getChoices());
        this.chosenAnswer = question.getChosenAnswer();
        this.opponentsGuess = question.getOpponentsGuess();
    }
}
