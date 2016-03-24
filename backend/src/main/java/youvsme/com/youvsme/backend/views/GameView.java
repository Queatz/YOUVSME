package youvsme.com.youvsme.backend.views;

import java.util.ArrayList;
import java.util.List;

import youvsme.com.youvsme.backend.models.GameModel;
import youvsme.com.youvsme.backend.models.GameUserQuestionModel;
import youvsme.com.youvsme.backend.models.UserModel;
import youvsme.com.youvsme.backend.services.ModelService;

/**
 * Created by jacob on 3/24/16.
 */
public class GameView {
    public String id;
    public String wager;
    public String wagerNote;
    public UserView user;
    public UserView opponent;
    public List<QuestionView> questions;

    public GameView(final UserModel me, final GameModel game) {
        this.id = game.getId();
        this.wager = game.getWager();
        this.wagerNote = game.getWagerNote();
        this.questions = new ArrayList<>();
        this.user = new UserView(me);

        final List<UserModel> users = game.getUsers();

        for (UserModel u : users) {
            if (me.getId().equals(u.getId())) {
                continue;
            }

            this.opponent = new UserView(u);
        }

        List<GameUserQuestionModel> questions = ModelService
                .get(GameUserQuestionModel.class)
                .filter("game", game)
                .list();

        for (GameUserQuestionModel q : questions) {
            this.questions.add(new QuestionView(q));
        }
    }
}
