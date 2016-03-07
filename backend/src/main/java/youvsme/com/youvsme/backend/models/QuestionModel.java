package youvsme.com.youvsme.backend.models;

import com.googlecode.objectify.annotation.Entity;

import java.util.List;

/**
 * Created by jacob on 2/25/16.
 */

@Entity
public class QuestionModel extends Model {
    public String text;
    public List<String> choices;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }
}
