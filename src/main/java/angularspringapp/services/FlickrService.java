package angularspringapp.services;

import angularspringapp.util.TimeUtil;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.commons.io.IOUtils;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FlickrApi;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FlickrService {

    private String key;
    private String secret;
    private String callback;
    OAuthService service;
    private Token requestToken;

    public void init() {
        service = new ServiceBuilder()
                .provider(FlickrApi.class)
                .apiKey(key)
                .apiSecret(secret)
                .callback(callback)
                .build();
    }

    public String getAuthorizationUrl() {
        requestToken = service.getRequestToken();
        return service.getAuthorizationUrl(requestToken);
    }

    public Map<String, String> getAccessToken(String stringVerifier) {
        Verifier verifier = new Verifier(stringVerifier);
        Token accessToken = service.getAccessToken(requestToken, verifier);
        Map<String, String> tokenInfo = new HashMap<String, String>();
        tokenInfo.put("token", accessToken.getToken());
        tokenInfo.put("secret", accessToken.getSecret());
        return tokenInfo;
    }

    public String getActivity(Map<String, String> tokenInfo) throws OAuthExpectationFailedException, OAuthCommunicationException, OAuthMessageSignerException, IOException {
        String params = "&timeframe=1d&per_page=50";
        return executeMethod(tokenInfo, "flickr.activity.userPhotos", params);
    }

    public String getUserPhotos(Map<String, String> tokenInfo, String userId, int page) throws OAuthExpectationFailedException, OAuthCommunicationException, OAuthMessageSignerException, IOException {
        String uploadDate = TimeUtil.getPureYesterdayDateDotSeparate();
        String params = String.format("&extras=description,date_upload,owner_name,last_update,tags,views,media,url_sq,url_n&min_upload_date=%s&user_id=%s&page=%s", uploadDate, userId, page);
        return executeMethod(tokenInfo, "flickr.people.getPhotos", params);
    }

    public String getUserContacts(Map<String, String> tokenInfo, int page) throws OAuthExpectationFailedException, OAuthCommunicationException, OAuthMessageSignerException, IOException {
        String params = String.format("&page=%s", page);
        return executeMethod(tokenInfo, "flickr.contacts.getList", params);
    }

    public String getUserInfoByToken(Map<String, String> tokenInfo) throws OAuthExpectationFailedException, OAuthCommunicationException, OAuthMessageSignerException, IOException {
        String params = "&oauth_token=" + tokenInfo.get("token");
        return executeMethod(tokenInfo, "flickr.auth.oauth.checkToken", params);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    private String executeMethod(Map<String, String> tokenInfo, String method, String params) throws IOException, OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException {

        HttpURLConnection connection = null;

        URL url = new URL("http://api.flickr.com/services/rest/?api_key=" + key + "&format=json&nojsoncallback=1&method=" + method + params);

        try {
            OAuthConsumer consumer = new DefaultOAuthConsumer(key, this.secret);
            consumer.setTokenWithSecret(tokenInfo.get("token"), tokenInfo.get("secret"));

            connection = (HttpURLConnection) url.openConnection();
            consumer.sign(connection);

            connection.connect();

            return IOUtils.toString(connection.getInputStream(), "UTF-8");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}

