package angularspringapp.services.neo.request;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RemoveNode extends AbstractRestCommand {

    public RemoveNode(String serverURI) {
        super(serverURI);
    }

    @Override
    protected URL getRequestURL() throws MalformedURLException {
        return new URL(getServerURI() + "node/");
    }

    @Override
    protected void readResponse(HttpURLConnection connection) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
