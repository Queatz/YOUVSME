package youvsme.com.youvsme.backend.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import youvsme.com.youvsme.backend.Grab;

/**
 * Created by jacob on 2/25/16.
 */
public class RootAbstractEndpoint implements Api {
    private static Map<String, Api> mappings = new HashMap<>();

    static {
        mappings.put("me", Grab.grab(MeEndpoint.class));
        mappings.put("challenge", Grab.grab(ChallengeEndpoint.class));
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

        throw new RuntimeException("no");
    }
}
