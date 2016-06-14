package youvsme.com.youvsme.backend.services;

import youvsme.com.youvsme.backend.models.GameModel;
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
}