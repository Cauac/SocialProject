package angularspringapp.services.neo.request;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public abstract class AbstractRestCommand {

    protected Logger logger = Logger.getLogger(this.getClass());
    private String ServerURI;

    class HTTPMethods {
        public static final String POST = "POST";
        public static final String DELETE = "DELETE";
        public static final String GET = "GET";
    }

    class RequestProperty {
        public static final String ACCEPT = "Accept";
        public static final String CONTENT_TYPE = "Content-Type";
    }

    protected static final String CHARSET = "UTF-8";
    protected static final String RESPONSE_TYPE = "application/json; charset=" + CHARSET;
    protected static final String CONTENT_TYPE = "application/json";

    public AbstractRestCommand(String serverURI) {
        ServerURI = serverURI;
    }

    public void execute() {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) getRequestURL().openConnection();
            configureConnection(connection);
            JSONObject requestObject = getRequestObject();
            if (requestObject != null) {
                OutputStream output = connection.getOutputStream();
                try {
                    output.write(requestObject.toJSONString().getBytes(CHARSET));
                } finally {
                    output.close();
                }
            }
            connection.connect();
            readResponse(connection);
            System.out.println(connection.getResponseCode());
        } catch (IOException e) {
            logger.error("Execute request error");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    protected void configureConnection(HttpURLConnection connection) throws ProtocolException {
        connection.setRequestMethod(HTTPMethods.POST);
        connection.setRequestProperty(RequestProperty.ACCEPT, RESPONSE_TYPE);
    }

    public String getServerURI() {
        return ServerURI;
    }

    protected JSONObject getRequestObject() {
        return null;
    }

    protected abstract URL getRequestURL() throws MalformedURLException;


    protected abstract void readResponse(HttpURLConnection connection) throws IOException;


}
