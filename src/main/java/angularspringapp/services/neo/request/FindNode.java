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

public class FindNode extends AbstractRestCommand {

    private String label;
    private String property;
    private String value;
    private long nodeId = -1;

    public FindNode(String serverURI, String label, String property, String value) {
        super(serverURI);
        this.label = label;
        this.property = property;
        this.value = value;
    }

    @Override
    protected URL getRequestURL() throws MalformedURLException {
        return new URL(getServerURI() + "label/" + label + "/nodes?" + property + "=%22" + value + "%22");
    }

    @Override
    protected void configureConnection(HttpURLConnection connection) throws ProtocolException {
        super.configureConnection(connection);
        connection.setRequestMethod(HTTPMethods.GET);
    }

    @Override
    protected void readResponse(HttpURLConnection connection) throws IOException {
        if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
            try {
                JSONParser parser = new JSONParser();
                JSONArray parsedResult = (JSONArray) parser.parse(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                if (parsedResult.size() > 0) {
                    JSONObject obj = (JSONObject) (parsedResult).get(0);
                    String selfLink = (String) obj.get("self");
                    nodeId = Long.parseLong(selfLink.replace(getServerURI() + "node/", ""));
                }
            } catch (ParseException e) {
                logger.error("Error parse neo response");
            }
        }
    }

    public long getNodeId() {
        return nodeId;
    }
}
