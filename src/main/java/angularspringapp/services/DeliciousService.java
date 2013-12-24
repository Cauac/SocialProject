package angularspringapp.services;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.YahooApi;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

public class DeliciousService {

    private String key;
    private String secret;
    private String callback;
    OAuthService service;
    private Token requestToken;

    public void init(){
        service = new ServiceBuilder()
                .provider(YahooApi.class)
                .apiKey(key)
                .apiSecret(secret)
                .callback(callback)
                .build();
    }

    public String getAuthorizationUrl(){
        requestToken = service.getRequestToken();
        return service.getAuthorizationUrl(requestToken);
    }

    public String getAccessToken(String stringVerifier){
        Verifier verifier = new Verifier(stringVerifier);
        Token accessToken = service.getAccessToken(requestToken, verifier);
        getLinks(accessToken);
        return accessToken.getToken();
    }

    public void getLinks(Token accessToken){
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.del.icio.us/v2/posts/get?tag=programming");
        service.signRequest(accessToken,request);
        Response response = request.send();
        System.out.println(response.getCode());
        System.out.println(response.getBody());
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
}
