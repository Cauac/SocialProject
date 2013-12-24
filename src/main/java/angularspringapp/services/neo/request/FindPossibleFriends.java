package angularspringapp.services.neo.request;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindPossibleFriends extends AbstractRestCommand {

    private String userId;
    private List<Map> friends = new ArrayList<Map>();

    public FindPossibleFriends(String serverURI, String userId) {
        super(serverURI);
        this.userId = userId;
    }

    @Override
    protected URL getRequestURL() throws MalformedURLException {
        return new URL(getServerURI() + "cypher");
    }

    @Override
    protected JSONObject getRequestObject() {
        JSONObject requestObject = new JSONObject();
        requestObject.put("query", "MATCH (n:User), (n)-[:FRIEND]->(f)-[:FRIEND]->(ff) " +
                "WHERE n.flickr_nsid='" + userId + "' and not((n)-[:FRIEND]->(ff)) " +
                "RETURN DISTINCT ff.flickr_nsid as possibleFriend, collect(f.flickr_nsid) as commonFriend, COUNT(*) " +
                "ORDER BY COUNT(*) DESC");
        return requestObject;
    }

    @Override
    protected void configureConnection(HttpURLConnection connection) throws ProtocolException {
        super.configureConnection(connection);
        connection.setDoOutput(true);
        connection.setRequestProperty(RequestProperty.CONTENT_TYPE, CONTENT_TYPE);
    }

    @Override
    protected void readResponse(HttpURLConnection connection) throws IOException {
        try {
            JSONParser parser = new JSONParser();
            JSONObject response = (JSONObject) parser.parse(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            JSONArray data = (JSONArray) response.get("data");
            for (Object dataItem : data) {
                Map resultItem = new HashMap();
                resultItem.put("possibleFriendId", (String) ((JSONArray) dataItem).get(0));
                resultItem.put("commonFriendId", ((JSONArray) ((JSONArray) dataItem).get(1)).toArray());
                resultItem.put("commonFriendCount", (Long) ((JSONArray) dataItem).get(2));
                friends.add(resultItem);
            }
        } catch (ParseException e) {
            logger.error("Error parse neo response");
        }
    }

    public List<Map> getFriendIds() {
        return friends;
    }
}
