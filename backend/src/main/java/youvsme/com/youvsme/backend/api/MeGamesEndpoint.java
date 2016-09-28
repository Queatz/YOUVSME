package youvsme.com.youvsme.backend.api;

import com.google.api.client.http.HttpMethods;
import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.googlecode.objectify.cmd.Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import youvsme.com.youvsme.backend.Config;
import youvsme.com.youvsme.backend.Grab;
import youvsme.com.youvsme.backend.models.GameModel;
import youvsme.com.youvsme.backend.models.UserModel;
import youvsme.com.youvsme.backend.services.JsonService;
import youvsme.com.youvsme.backend.services.ModelService;
import youvsme.com.youvsme.backend.services.UserService;
import youvsme.com.youvsme.backend.views.GameView;

/**
 * Created by jacob on 9/27/16.
 */

public class MeGamesEndpoint implements Api {

    @Override
    public void serve(String method, List<String> path, HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // GET is only allowed here
        if (!HttpMethods.GET.equals(method)) {
            resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        final UserModel me = Grab.grab(UserService.class)
                .userFromToken(req.getParameter(Config.PARAM_TOKEN));

        // Not auth'd
        if (me == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


        List<GameModel> games;

        Query<GameModel> query = ModelService.get(GameModel.class);
        games = query.filter("users", me)
                .filter("complete", false)
                .order("-created")
                .list();

        List<GameView> gameViews = new ArrayList<>();

        for (GameModel game : games) {
            gameViews.add(new GameView(me, game));
        }

        resp.getWriter().write(Grab.grab(JsonService.class).json(gameViews));
    }
}
