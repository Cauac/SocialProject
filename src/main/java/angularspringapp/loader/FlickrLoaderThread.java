package angularspringapp.loader;

import angularspringapp.dao.MongoUserDAO;
import angularspringapp.services.jms.JMSMessageSender;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component(value = "flickrLoaderThread")
public class FlickrLoaderThread extends Thread {

    private static final Logger logger = Logger.getLogger(FlickrLoaderThread.class);

    @Autowired
    MongoUserDAO userDAO;

    @Autowired
    FlickrLoader loader;

//    @Autowired
//    JMSMessageSender messageSender;

    @Override
    public void run() {
        boolean runAnalyzer;
        while (true) {
            BasicDBList users = userDAO.readUsers();
            boolean uploadNewData;
            runAnalyzer = false;
            for (Object usersObject : users) {
                DBObject user = (DBObject) usersObject;
                String login = (String) user.get("_id");
                try {
                    uploadNewData = loader.loadAndSaveUserActivity(login);
                    if (uploadNewData) {
//                        messageSender.sendUploadByUserMessage(login);
                        runAnalyzer = true;
                    }
                } catch (OAuthExpectationFailedException e) {
                    logger.error(e.getMessage());
                } catch (OAuthCommunicationException e) {
                    logger.error(e.getMessage());
                } catch (OAuthMessageSignerException e) {
                    logger.error(e.getMessage());
                } catch (IOException e) {
                    logger.error(e.getMessage());
                } catch (ParseException e) {
                    logger.error(e.getMessage());
                }
            }
            if (runAnalyzer) {
//                messageSender.sendUploadCompleteMessage();
            }
        }
    }
}
