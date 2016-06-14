/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package youvsme.com.youvsme.backend;

import com.google.api.client.http.HttpMethods;
import com.google.api.client.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.*;

import youvsme.com.youvsme.backend.api.RootAbstractEndpoint;

public class MyServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        direct(HttpMethods.GET, req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        direct(HttpMethods.POST, req, resp);
    }

    private void direct(String method, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");

        String[] pathRaw = req.getRequestURI().split("/");

        if (pathRaw.length < 2) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if ("add-question".equals(pathRaw[1])) {
            raw("./add-question.html", resp);

        } else if ("contest".equals(pathRaw[1])) {
            raw("./contest.html", resp);

        } else if("api".equals(pathRaw[1])) {
            if (pathRaw.length < 3) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            resp.setContentType("application/json");

            // Skip initial /api/
            List<String> path = Arrays.asList(pathRaw).subList(2, pathRaw.length);

            Grab.grab(RootAbstractEndpoint.class).serve(method, path, req, resp);
        } else {
            raw("./index.html", resp);
        }
    }

    private void raw(String relativeWebPath, HttpServletResponse resp) throws IOException {
        String absoluteFilePath = getServletContext().getRealPath(relativeWebPath);
        File file = new File(absoluteFilePath);
        IOUtils.copy(new FileInputStream(file), resp.getOutputStream());
        resp.setContentType("text/html");
    }
}
