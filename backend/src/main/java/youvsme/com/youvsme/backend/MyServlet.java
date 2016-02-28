/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package youvsme.com.youvsme.backend;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.*;

import youvsme.com.youvsme.backend.api.RootAbstractEndpoint;

public class MyServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        direct("GET", req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        direct("POST", req, resp);
    }

    private void direct(String method, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String[] pathRaw = req.getRequestURI().split("/");

        if (pathRaw.length < 3) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Skip initial /api/
        List<String> path = Arrays.asList(pathRaw).subList(2, pathRaw.length);

        Grab.grab(RootAbstractEndpoint.class).serve(method, path, req, resp);
    }
}
