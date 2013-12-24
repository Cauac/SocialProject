package angularspringapp.services.neo;

import angularspringapp.services.neo.request.CreateNode;
import angularspringapp.services.neo.request.CreateRelationship;
import angularspringapp.services.neo.request.FindNode;

import angularspringapp.services.neo.request.FindPossibleFriends;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NeoService {

    private static final String SERVER_ROOT_URI = "http://localhost:7474/db/data/";

    public long saveUser(Map<String, Object> userFields) {
        JSONObject user = new JSONObject();
        user.putAll(userFields);
        CreateNode request = new CreateNode(SERVER_ROOT_URI, "User", user);
        request.execute();
        return request.getCreatedNodeId();
    }

    public long findFlickrUserNode(String userId) {
        FindNode restCommand = new FindNode(SERVER_ROOT_URI, "User", "flickr_nsid", userId);
        restCommand.execute();
        return restCommand.getNodeId();
    }

    public void saveFlickrFriendRelationship(String fromId, String toId) {
        long neoFromId = findFlickrUserNode(fromId);
        long neoToId = findFlickrUserNode(toId);
        if (neoFromId < 0) {
            Map<String, Object> user = new HashMap<String, Object>();
            user.put("flickr_nsid", fromId);
            neoFromId = saveUser(user);
        }
        if (neoToId < 0) {
            Map<String, Object> user = new HashMap<String, Object>();
            user.put("flickr_nsid", toId);
            neoToId = saveUser(user);
        }
        CreateRelationship restCommand = new CreateRelationship(SERVER_ROOT_URI, "FRIEND", neoFromId, neoToId);
        restCommand.execute();
    }

    public List<Map> findPossibleFriends(String userId) {
        FindPossibleFriends restCommand = new FindPossibleFriends(SERVER_ROOT_URI, userId);
        restCommand.execute();
        return restCommand.getFriendIds();
    }
}
