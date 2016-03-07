package youvsme.com.youvsme.backend.api;

import com.google.api.client.http.HttpMethods;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import youvsme.com.youvsme.backend.Config;
import youvsme.com.youvsme.backend.Grab;
import youvsme.com.youvsme.backend.models.ChallengeModel;
import youvsme.com.youvsme.backend.models.UserModel;
import youvsme.com.youvsme.backend.push.ChallengePush;
import youvsme.com.youvsme.backend.push.KickInTheFacePush;
import youvsme.com.youvsme.backend.push.Push;
import youvsme.com.youvsme.backend.services.JsonService;
import youvsme.com.youvsme.backend.services.ModelService;
import youvsme.com.youvsme.backend.services.PushService;
import youvsme.com.youvsme.backend.services.UserService;

/**
 * Created by jacob on 2/25/16.
 */
public class ChallengeEndpoint implements Api {
    @Override
    public void serve(String method, List<String> path, HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // Authenticate
        UserModel me = Grab.grab(UserService.class).userFromToken(req.getParameter(Config.PARAM_TOKEN));

        if (me == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        switch (method) {
            case HttpMethods.GET:


                break;
            case HttpMethods.POST:

                // New challenge
                if (path.isEmpty()) {
                    final String wager = req.getParameter(Config.PARAM_WAGER_WHAT);
                    final String wagerNote = req.getParameter(Config.PARAM_WAGER_NOTE);
                    final String opponentId = req.getParameter(Config.PARAM_OPPONENT);

                    UserModel opponent = ModelService.get(UserModel.class).filter("id", opponentId).first().now();

                    if (opponent == null) {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }

                    // TODO check that they are friends?

                    final ChallengeModel challenge = ModelService.create(ChallengeModel.class);
                    challenge.setUsers(ImmutableList.of(me, opponent));
                    challenge.setActive(true);
                    challenge.setWager(wager);
                    challenge.setWagerNote(wagerNote);
                    ModelService.save(challenge);

                    Grab.grab(PushService.class).send(opponent,
                            new ChallengePush(me.getFirstName(), !Strings.isNullOrEmpty(wager)));

                    resp.getWriter().write(Grab.grab(JsonService.class).json(challenge));

                    return;
                }

                // POST to a challenge

                if (path.size() > 0) {
                    ChallengeModel challenge = ModelService.get(ChallengeModel.class)
                            .filter("id", path.get(0))
                            .filter("users", me)
                            .first().now();

                    if (challenge == null) {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }

                    if (path.size() > 1) {
                        if ("kick-in-the-face".equals(path.get(1))) {
                            Push push = new KickInTheFacePush(me.getFirstName());

                            for (UserModel opponent : challenge.getUsers()) {
                                if (me.getId().equals(opponent.getId())) {
                                    continue;
                                }

                                Grab.grab(PushService.class).send(opponent, push);
                            }
                        }
                    }
                }


                break;
        }
    }
}
