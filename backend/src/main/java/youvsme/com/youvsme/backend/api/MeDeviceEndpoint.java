package youvsme.com.youvsme.backend.api;

import com.google.common.base.Strings;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import youvsme.com.youvsme.backend.Grab;
import youvsme.com.youvsme.backend.models.UserDeviceModel;
import youvsme.com.youvsme.backend.models.UserModel;
import youvsme.com.youvsme.backend.services.ModelService;
import youvsme.com.youvsme.backend.services.UserService;

/**
 * Created by jacob on 3/5/16.
 */
public class MeDeviceEndpoint implements Api {
    UserService userService = Grab.grab(UserService.class);

    @Override
    public void serve(String method, List<String> path, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserModel me = userService.userFromToken(req.getParameter("token"));

        if (me == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String deviceToken = req.getParameter("token");

        if (Strings.isNullOrEmpty(deviceToken)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        UserDeviceModel deviceModel = ModelService.get(UserDeviceModel.class)
                .filter("user", me).filter("deviceToken", deviceToken).first().now();

        if (deviceModel != null) {
            deviceModel = ModelService.create(UserDeviceModel.class);
            deviceModel.setUser(me);
            deviceModel.setDeviceToken(deviceToken);
            ModelService.save(deviceModel);
        }
    }
}
