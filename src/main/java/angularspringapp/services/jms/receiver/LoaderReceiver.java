package angularspringapp.services.jms.receiver;

import angularspringapp.analyzer.FlickrAnalyzer;
import org.apache.log4j.Logger;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import angularspringapp.services.jms.MessageDetails.*;
import org.springframework.beans.factory.annotation.Autowired;


public class LoaderReceiver implements MessageListener {

    private static final Logger logger = Logger.getLogger(LoaderReceiver.class);

    @Autowired
    FlickrAnalyzer flickrAnalyzer;

    @Override
    public void onMessage(Message message) {
        logger.info("Start receive message");
        if (message instanceof MapMessage) {
            final MapMessage mapMessage = (MapMessage) message;

            try {
                String type = mapMessage.getString(Fields.TYPE);
                if (Values.UPLOAD_NEW_DATA_BY_USER.equals(type)) {
                    String username = mapMessage.getString(Fields.USERNAME);
                    flickrAnalyzer.analyzePhotoActivityByUser(username);
                } else {
                    flickrAnalyzer.analyzeActivity();
                }
                message.acknowledge();
                logger.info("Receive message type: " + type);
            } catch (JMSException e) {
                logger.error(e.getMessage());
            }
        }
    }
}
