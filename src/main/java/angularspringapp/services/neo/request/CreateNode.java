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

public class CreateNode extends AbstractRestCommand {

    private String label;
    private JSONObject properties;
    private long createdNodeId = -1;

    public CreateNode(String serverURI, String label, JSONObject properties) {
        super(serverURI);
        this.label = label;
        this.properties = properties;
    }

    @Override
    protected JSONObject getRequestObject() {
        JSONObject requestObject = new JSONObject();
        JSONObject params = new JSONObject();
        requestObject.put("query", "CREATE (n:" + label + " { props } ) RETURN ID(n)");
        params.put("props", properties);
        requestObject.put("params", params);
        return requestObject;
    }

    @Override
    protected URL getRequestURL() throws MalformedURLException {
        return new URL(getServerURI() + "cypher");
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
            createdNodeId = (Long) ((JSONArray) ((JSONArray) response.get("data")).get(0)).get(0);
        } catch (ParseException e) {
            logger.error("Error parse neo response");
        }
    }

    public long getCreatedNodeId() {
        return createdNodeId;
    }
}
