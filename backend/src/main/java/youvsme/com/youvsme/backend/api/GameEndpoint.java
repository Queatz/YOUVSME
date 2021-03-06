package youvsme.com.youvsme.backend.api;

import com.google.api.client.http.HttpMethods;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.Ref;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import youvsme.com.youvsme.backend.Config;
import youvsme.com.youvsme.backend.Grab;
import youvsme.com.youvsme.backend.models.GameModel;
import youvsme.com.youvsme.backend.models.GameQuestionModel;
import youvsme.com.youvsme.backend.models.QuestionModel;
import youvsme.com.youvsme.backend.models.UserModel;
import youvsme.com.youvsme.backend.push.ChallengePush;
import youvsme.com.youvsme.backend.push.FinishedAnsweringPush;
import youvsme.com.youvsme.backend.push.KickInTheFacePush;
import youvsme.com.youvsme.backend.push.Push;
import youvsme.com.youvsme.backend.services.GameService;
import youvsme.com.youvsme.backend.services.JsonService;
import youvsme.com.youvsme.backend.services.ModelService;
import youvsme.com.youvsme.backend.services.PushService;
import youvsme.com.youvsme.backend.services.UserService;
import youvsme.com.youvsme.backend.views.CorrectView;
import youvsme.com.youvsme.backend.views.GameView;

/**
 * Created by jacob on 2/25/16.
 */
public class GameEndpoint implements Api {
    @Override
    public void serve(String method, List<String> path, HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // Authenticate
        UserModel me = Grab.grab(UserService.class).userFromToken(req.getParameter(Config.PARAM_TOKEN));

        if (me == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        switch (method) {
            case HttpMethods.GET:


                break;
            case HttpMethods.POST:

                // New game
                if (path.isEmpty()) {
                    final String wager = req.getParameter(Config.PARAM_WAGER_WHAT);
                    final String wagerNote = req.getParameter(Config.PARAM_WAGER_NOTE);
                    final String opponentId = req.getParameter(Config.PARAM_OPPONENT);

                    UserModel opponent = ModelService.get(UserModel.class).id(opponentId).now();

                    if (opponent == null) {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }

                    // TODO check that they are friends?

                    GameService gameService = Grab.grab(GameService.class);
                    GameModel activeGame = gameService.latestGameBetween(me, opponent);

                    // If there's an active game between these people, just return it and don't
                    // create a new one.  This does mean that the wager is dismissed, but normally
                    // they should not be able to get into this state where they can create a game.
                    if (activeGame != null && gameService.questionsRemaining(activeGame) > 0) {
                        resp.getWriter().write(Grab.grab(JsonService.class).json(new GameView(me, activeGame)));
                        return;
                    }


                    final GameModel game = ModelService.create(GameModel.class);
                    game.setUsers(ImmutableList.of(ModelService.ref(me), ModelService.ref(opponent)));
                    game.setWager(wager);
                    game.setWagerNote(wagerNote);
                    ModelService.save(game);

                    // Add questions

                    int totalQuestions = ModelService.get(QuestionModel.class).count();
                    int randomTries = 0;
                    Set<Integer> pickedQuestions = new HashSet<>();

                    // TODO not fail-safe, demands existence of all question IDs 0-count
                    for (int i = 0; i < 5; i++) {
                        int random = new Random().nextInt(totalQuestions);

                        if (pickedQuestions.contains(random)) {
                            // Give up trying to get 5 uniquely random questions
                            // If this happens, the game will not have 5 questions in it
                            if (randomTries > 10) {
                                break;
                            }

                            randomTries++;

                            continue;
                        }

                        pickedQuestions.add(random);
                        randomTries = 0;

                        QuestionModel question = ModelService
                                .get(QuestionModel.class, Integer.toString(random));

                        for (UserModel user : new UserModel[] {me, opponent}) {
                            GameQuestionModel answer = ModelService.create(GameQuestionModel.class);
                            answer.setUser(ModelService.ref(user));
                            answer.setGame(ModelService.ref(game));
                            answer.setQuestion(ModelService.ref(question));
                            ModelService.save(answer);
                        }
                    }

                    resp.getWriter().write(Grab.grab(JsonService.class).json(new GameView(me, game)));

                    return;
                }

                // POST to a game

                else if (path.size() > 0) {
                    GameModel game = ModelService.get(GameModel.class).id(path.get(0)).now();

                    if (game == null || !game.getUsers().contains(ModelService.ref(me))) {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }

                    if (path.size() > 1) {
                        if ("kick-in-the-face".equals(path.get(1))) {
                            Push push = new KickInTheFacePush(game.getId(), me.getFirstName());

                            for (Ref<UserModel> opponent : game.getUsers()) {
                                if (me.getId().equals(opponent.getKey().getName())) {
                                    continue;
                                }

                                Grab.grab(PushService.class).send(opponent.get(), push);
                            }
                        }

                        // /game/10/answer/10/3
                        else if ("answer".equals(path.get(1))) {
                            GameQuestionModel question = ModelService.get(GameQuestionModel.class)
                                    .id(path.get(2)).now();

                            // Authenticate
                            if (question == null || !question.getGame().get().getUsers().contains(ModelService.ref(me))) {
                                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                return;
                            }

                            if (question.getChosenAnswer() != null) {
                                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                return;
                            }

                            question.setChosenAnswer(Integer.parseInt(path.get(3)));
                            ModelService.save(question);

                            notifyDone(me, game);
                        }
                        // /game/10/guess/10/3
                        else if ("guess".equals(path.get(1))) {
                            GameQuestionModel question = ModelService.get(GameQuestionModel.class)
                                    .id(path.get(2)).now();

                            // Authenticate
                            if (question == null || !question.getGame().get().getUsers().contains(ModelService.ref(me))) {
                                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                return;
                            }

                            if (question.getOpponentsGuess() != null) {
                                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                return;
                            }

                            question.setOpponentsGuess(Integer.parseInt(path.get(3)));
                            ModelService.save(question);

                            boolean correct = question.getOpponentsGuess().equals(question.getChosenAnswer());
                            resp.getWriter().write(Grab.grab(JsonService.class).json(new CorrectView(correct)));

                            notifyDone(me, game);
                        }
                    }
                }


                break;
        }
    }

    private void notifyDone(UserModel me, GameModel game) {
        List<GameQuestionModel> questions = ModelService.get(GameQuestionModel.class)
                .filter("game", game).list();

        boolean meDoneGuessing = true;
        boolean opDoneChoosing = true;
        boolean opDoneGuessing = true;

        for (GameQuestionModel q : questions) {
            if (me.getId().equals(q.getUser().getKey().getName())) {
                if(q.getChosenAnswer() == null) {
                    // Not at least done choosing mine yet
                    return;
                }

                if (q.getOpponentsGuess() == null) {
                    opDoneGuessing = false;
                }
            } else {
                if (q.getOpponentsGuess() == null) {
                    meDoneGuessing = false;
                }

                if (q.getChosenAnswer() == null) {
                    opDoneChoosing = false;
                }
            }
        }

        Push push;

        // Push (to: opponent) -> I finish choosing -> I challenged you
        // Push (to: me) -> She finishes choosing and answering -> She finished answering
        // Push (to: opponent) I finished answering -> See who won

        if (opDoneChoosing && opDoneGuessing && meDoneGuessing) {
            Grab.grab(GameService.class).markGameAsComplete(game);
            push = new FinishedAnsweringPush(game.getId(), me.getFirstName(), true);
        } else if (opDoneChoosing && meDoneGuessing) {
            push = new FinishedAnsweringPush(game.getId(), me.getFirstName(), false);
        } else if (!opDoneChoosing) {
            push = new ChallengePush(game.getId(), me.getFirstName(), !Strings.isNullOrEmpty(game.getWager()));
        } else {
            return;
        }

        for (Ref<UserModel> opponent : game.getUsers()) {
            if (me.getId().equals(opponent.getKey().getName())) {
                continue;
            }

            Grab.grab(PushService.class).send(opponent.get(), push);
        }
    }
}
