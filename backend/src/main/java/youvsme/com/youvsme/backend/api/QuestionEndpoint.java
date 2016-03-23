package youvsme.com.youvsme.backend.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import youvsme.com.youvsme.backend.models.QuestionModel;
import youvsme.com.youvsme.backend.services.ModelService;

/**
 * Created by jacob on 3/22/16.
 */
public class QuestionEndpoint implements Api {

    @Override
    public void serve(String method, List<String> path, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        QuestionModel question = ModelService.create(QuestionModel.class);
        question.setText(req.getParameter("text"));

        List<String> choices = new ArrayList<>();
        for (String c : new String[] { "a", "b", "c", "d" }) {
            choices.add(req.getParameter(c));
        }

        question.setChoices(choices);

        String id = Integer.toString(ModelService.get(QuestionModel.class).count());

        question.setId(id);

        ModelService.save(question);
    }
}