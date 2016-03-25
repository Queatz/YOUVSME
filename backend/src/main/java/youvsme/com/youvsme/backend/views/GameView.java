package youvsme.com.youvsme.backend.views;

import com.google.appengine.repackaged.com.google.common.collect.ImmutableList;
import com.googlecode.objectify.Ref;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import youvsme.com.youvsme.backend.models.GameModel;
import youvsme.com.youvsme.backend.models.GameQuestionModel;
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
    public Date created;

    public GameView(final UserModel me, final GameModel game) {
        this.id = game.getId();
        this.wager = game.getWager();
        this.wagerNote = game.getWagerNote();
        this.questions = new ArrayList<>();
        this.user = new UserView(me);
        this.created = game.created;

        final List<UserModel> users = new ArrayList<>();

        for (Ref<UserModel> ref : game.getUsers()) {
            users.add(ref.get());
        }

        for (UserModel u : users) {
            if (me.getId().equals(u.getId())) {
                continue;
            }

            this.opponent = new UserView(u);
        }

        List<GameQuestionModel> questions = ModelService
                .get(GameQuestionModel.class)
                .filter("game", game)
                .list();

        for (GameQuestionModel q : questions) {
            this.questions.add(new QuestionView(q));
        }
    }
}
