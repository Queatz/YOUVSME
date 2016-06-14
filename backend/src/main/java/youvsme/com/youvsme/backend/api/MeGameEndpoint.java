package youvsme.com.youvsme.backend.api;

import com.google.api.client.http.HttpMethods;
import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.cmd.Query;

import java.io.IOException;
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
 * Created by jacob on 2/25/16.
 */
public class MeGameEndpoint implements Api {

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


        GameModel game;

        if (path.size() == 1) {
            game = ModelService.get(GameModel.class).id(path.get(0)).now();
        } else {
            Query<GameModel> query = ModelService.get(GameModel.class);
            game = query.filter("users", me).order("-created").first().now();;
        }

        if (game == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        resp.getWriter().write(Grab.grab(JsonService.class).json(new GameView(me, game)));
    }
}
