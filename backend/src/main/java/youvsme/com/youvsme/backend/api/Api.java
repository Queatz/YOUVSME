package youvsme.com.youvsme.backend.api;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by jacob on 2/25/16.
 */
public interface Api {
    void serve(String method, List<String> path, HttpServletRequest req, HttpServletResponse resp) throws IOException;
}
