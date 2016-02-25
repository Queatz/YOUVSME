package youvsme.com.youvsme.backend.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import youvsme.com.youvsme.backend.Grab;
import youvsme.com.youvsme.backend.models.UserModel;
import youvsme.com.youvsme.backend.services.UserService;

/**
 * Created by jacob on 2/25/16.
 */
public class MeEndpoint implements Api {
    private static Map<String, Api> mappings = new HashMap<>();

    static {
        mappings.put("friends", Grab.grab(MeFriendsEndpoint.class));
        mappings.put("active-challenge", Grab.grab(MeActiveChallengeEndpoint.class));
    }

    private boolean mapped(String method, List<String> path, HttpServletRequest req, HttpServletResponse resp) {
        if (path.size() > 0 && mappings.containsKey(path.get(0))) {
            try {
                mappings.get(path.get(0)).serve(method, path.subList(1, path.size()), req, resp);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        return false;
    }

    @Override
    public void serve(String method, List<String> path, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (mapped(method, path, req, resp)) {
            return;
        }

        switch (method) {
            case "GET":
                UserModel me = Grab.grab(UserService.class)
                        .userFromToken(req.getParameter("token"), req.getParameter("facebookToken"));

                if (me == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                } else {
                    resp.getWriter().write(Json.json(me));
                }

                return;
        }

        throw new RuntimeException("no");
    }
}
