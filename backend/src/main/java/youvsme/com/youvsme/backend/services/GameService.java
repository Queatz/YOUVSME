package youvsme.com.youvsme.backend.services;

import java.util.List;

import youvsme.com.youvsme.backend.models.GameModel;
import youvsme.com.youvsme.backend.models.GameQuestionModel;
import youvsme.com.youvsme.backend.models.UserModel;

/**
 * Created by jacob on 5/4/16.
 */
public class GameService {
    public GameModel latestGameBetween(UserModel me, UserModel user) {
        return ModelService.get(GameModel.class)
                .order("-created")
                .filter("users", me)
                .filter("users", user)
                .first()
                .now();
    }

    public int questionsRemaining(GameModel game) {
        List<GameQuestionModel> questions = ModelService.get(GameQuestionModel.class)
                .filter("game", game).list();

        int remaining = 0;

        for (GameQuestionModel q : questions) {
            if(q.getChosenAnswer() == null || q.getChosenAnswer() == null) {
                remaining++;
            }
        }

        return remaining;
    }

    public void markGameAsComplete(GameModel game) {
        game.setComplete(true);
        ModelService.save(game);
    }
}