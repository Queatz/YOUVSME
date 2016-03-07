package youvsme.com.youvsme.backend.api;

import com.google.api.client.http.HttpMethods;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import youvsme.com.youvsme.backend.Config;
import youvsme.com.youvsme.backend.Grab;
import youvsme.com.youvsme.backend.models.ChallengeModel;
import youvsme.com.youvsme.backend.models.UserModel;
import youvsme.com.youvsme.backend.services.JsonService;
import youvsme.com.youvsme.backend.services.ModelService;
import youvsme.com.youvsme.backend.services.UserService;

/**
 * Created by jacob on 2/25/16.
 */
public class MeChallengeEndpoint implements Api {
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

        ChallengeModel challenge = ModelService.get(ChallengeModel.class)
                .filter("users", me).filter("active", true).first().now();

        if (challenge == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        resp.getWriter().write(Grab.grab(JsonService.class).json(challenge));
    }
}
