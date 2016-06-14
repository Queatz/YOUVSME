package youvsme.com.youvsme.backend.views;

import youvsme.com.youvsme.backend.models.GameModel;
import youvsme.com.youvsme.backend.models.UserModel;

/**
 * Created by jacob on 5/4/16.
 */
public class UserGameView {
    public UserView user;
    public GameView game;

    public UserGameView(UserModel me, UserModel user, GameModel game) {
        this.user = new UserView(user);

        if (game != null) {
            this.game = new GameView(me, game);
        } else {
            this.game = null;
        }
    }
}
