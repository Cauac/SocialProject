package loader;

import angularspringapp.dao.MongoFlickrDAO;
import angularspringapp.loader.FlickrLoader;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/app-config.xml")
public class LoaderTest {

    @Autowired
    FlickrLoader flickrLoader;

    @Autowired
    MongoFlickrDAO flickrDAO;

    @Test
    public void simple() {
        try {
            flickrLoader.loadAndSaveUserActivity("anton");
//            flickrLoader.loadAndSaveUserActivity("kanton");
//            flickrLoader.loadAndSaveUserActivity("fanton");
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}