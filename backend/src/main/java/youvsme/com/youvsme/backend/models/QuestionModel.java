package youvsme.com.youvsme.backend.models;

import com.googlecode.objectify.annotation.Entity;

import java.util.List;

/**
 * Created by jacob on 2/25/16.
 */

@Entity
public class QuestionModel extends Model {
    public String text;
    public String author;
    public boolean vulgar;
    public List<String> choices;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isVulgar() {
        return vulgar;
    }

    public void setVulgar(boolean vulgar) {
        this.vulgar = vulgar;
    }

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }
}
