package youvsme.com.youvsme.services;

import youvsme.com.youvsme.YOUVSMEApp;

/**
 * Created by jacob on 2/28/16.
 */
public class GameService {

    private static GameService instance;
    public static GameService use() {
        if (instance == null) {
            synchronized (GameService.class) {
                if (instance == null) {
                    instance = new GameService();
                }
            }
        }

        return instance;
    }

    private YOUVSMEApp app;

    public void initialize(YOUVSMEApp youvsmeApp) {
        app = youvsmeApp;
    }

    public YOUVSMEApp getApp() {
        return app;
    }

    public enum GameState {
        NO_USER,
        NO_OPPONENT,
        ANSWERING_QUESTIONS,
        WAITING_FOR_OPPONENT,
        GAME_FINISHED
    }

    public GameState getState() {
        return GameState.NO_USER;
    }
}
