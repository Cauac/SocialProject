package angularspringapp.services.neo.request;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class CreateRelationship extends AbstractRestCommand {

    private String type;
    private JSONObject properties;
    private long fromId;
    private long toId;

    public CreateRelationship(String serverURI, String type, long from, long to) {
        super(serverURI);
        this.type = type;
        this.fromId = from;
        this.toId = to;
    }

    public void setProperties(JSONObject properties) {
        this.properties = properties;
    }

    @Override
    protected JSONObject getRequestObject() {
        JSONObject requestObject = new JSONObject();
        requestObject.put("to", getServerURI() + "node/" + toId);
        requestObject.put("type", type);
        if (properties != null) {
            requestObject.put("data", properties);
        }
        return requestObject;
    }

    @Override
    protected void configureConnection(HttpURLConnection connection) throws ProtocolException {
        super.configureConnection(connection);
        connection.setDoOutput(true);
        connection.setRequestProperty(RequestProperty.CONTENT_TYPE, CONTENT_TYPE);
    }

    @Override
    protected URL getRequestURL() throws MalformedURLException {
        return new URL(getServerURI()+"node/" + fromId + "/relationships");
    }

    @Override
    protected void readResponse(HttpURLConnection connection) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
