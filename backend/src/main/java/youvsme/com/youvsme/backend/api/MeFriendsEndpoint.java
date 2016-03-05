package youvsme.com.youvsme.backend.api;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.types.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import youvsme.com.youvsme.backend.Grab;
import youvsme.com.youvsme.backend.models.UserModel;
import youvsme.com.youvsme.backend.services.JsonService;
import youvsme.com.youvsme.backend.services.UserService;
import youvsme.com.youvsme.backend.views.UserModelView;

/**
 * Created by jacob on 2/25/16.
 */
public class MeFriendsEndpoint implements Api {
    UserService userService = Grab.grab(UserService.class);

    @Override
    public void serve(String method, List<String> path, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserModel me = userService.userFromToken(req.getParameter("token"));

        if (me == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // TODO don't hit facebook again if we refreshed their friends within the minute already
        FacebookClient facebookClient = new DefaultFacebookClient(me.getFacebookToken(), Version.LATEST);
        Connection<User> facebookFriends = facebookClient.fetchConnection("me/friends", User.class);

        List<UserModelView> friends = new ArrayList<>();

        for(User facebookFriend : facebookFriends.getData()) {
            UserModel user = userService.userFromFacebookUser(facebookFriend);

            friends.add(new UserModelView(user));
        }

        resp.getWriter().write(Grab.grab(JsonService.class).json(friends));
    }
}
