package angularspringapp.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@RequestMapping(value = "/tw")
@Controller
public class TwitterAuthController {

    @Autowired
    private TwitterConnectionFactory connectionFactory;

    private OAuthToken requestToken;
    private String authURL;

    @RequestMapping(value = "/getAuthUrl", method = RequestMethod.GET)
    public
    @ResponseBody
    String getAuthUrl() {
        OAuth1Operations oAuthOperations = connectionFactory.getOAuthOperations();
        requestToken = oAuthOperations.fetchRequestToken("http://localhost:8080/tw/getAccessToken", null);
        return oAuthOperations.buildAuthorizeUrl(requestToken.getValue(), OAuth1Parameters.NONE);
    }

    @RequestMapping(value = "/getAccessToken", method = RequestMethod.GET)
    public ModelAndView getAccessToken(String oauth_token, String oauth_verifier, HttpServletRequest request) {
        OAuth1Operations oAuthOperations = connectionFactory.getOAuthOperations();
        OAuthToken token = oAuthOperations.exchangeForAccessToken(new AuthorizedRequestToken(requestToken, oauth_verifier), null);
        Connection<Twitter> connection = connectionFactory.createConnection(token);
        long id = connection.getApi().userOperations().getProfileId();

        ModelAndView mv = new ModelAndView("redirect:/j_spring_service_security_check");
        mv.getModelMap().put("service_name", "twitter");
        mv.getModelMap().put("profile_id", id);
        return mv;
    }
}
