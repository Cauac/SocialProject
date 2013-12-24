package loader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/app-config.xml")
public class SocialTest {

//    http://localhost:8080/?oauth_token=orbc6tY7u5eIjqyrSlOh4qrAdSyGizYMBHhcdAOiAI&oauth_verifier=zwkkfuOhzh9TOxFafL4CMU3aCe8RYZR5Cv0Rb4kI3I
    @Autowired
    private TwitterConnectionFactory connectionFactory;

    @Test
    public void test() {
        OAuth1Operations oAuthOperations = connectionFactory.getOAuthOperations();
        OAuthToken requestToken = oAuthOperations.fetchRequestToken( "http://localhost:8080/", null );
//        String authorizeUrl = oAuthOperations.buildAuthorizeUrl(requestToken.getValue(), OAuth1Parameters.NONE );
        OAuthToken token = oAuthOperations.exchangeForAccessToken(new AuthorizedRequestToken(requestToken, "zwkkfuOhzh9TOxFafL4CMU3aCe8RYZR5Cv0Rb4kI3I"), null);
        System.out.println(token.getSecret());
        System.out.println(token.getValue());
//        System.out.println(authorizeUrl);
    }
}
