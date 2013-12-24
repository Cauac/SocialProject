package angularspringapp.services.jms;

import angularspringapp.util.TimeUtil;
import org.apache.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import angularspringapp.services.jms.MessageDetails.Fields;
import angularspringapp.services.jms.MessageDetails.Values;
import org.springframework.jms.core.MessagePostProcessor;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;


public class JMSMessageSender {
    private static final Logger logger = Logger.getLogger(JMSMessageSender.class);

    private final JmsTemplate loaderQueue;
    private final JmsTemplate mailQueue;

    public JMSMessageSender(final JmsTemplate loaderQueue, final JmsTemplate mailQueue) {
        this.loaderQueue = loaderQueue;
        this.loaderQueue.setDeliveryMode(DeliveryMode.PERSISTENT);
        this.loaderQueue.setTimeToLive(0);

        this.mailQueue = mailQueue;
        this.mailQueue.setDeliveryMode(DeliveryMode.PERSISTENT);
        this.mailQueue.setTimeToLive(0);
    }

    public void sendUploadCompleteMessage() {
        Map message = new HashMap();
        message.put(Fields.TYPE, Values.UPLOAD_NEW_DATA);
        loaderQueue.convertAndSend(message);
        logger.info("Send message type: " + Values.UPLOAD_NEW_DATA);
    }

    public void sendUploadByUserMessage(String username) {
        Map message = new HashMap();
        message.put(Fields.TYPE, Values.UPLOAD_NEW_DATA_BY_USER);
        message.put(Fields.USERNAME, username);
        loaderQueue.convertAndSend(message);
        logger.info("Send message type: " + Values.UPLOAD_NEW_DATA_BY_USER);
    }

    public void sendRestorePasswordMessage(String mail, String login, String password) {
        Map message = new HashMap();
        message.put(Fields.MAIL, mail);
        message.put(Fields.PASSWORD, password);
        message.put(Fields.USERNAME, login);
        mailQueue.convertAndSend(message, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws JMSException {
                message.setStringProperty(Fields.TYPE, Values.SEND_MESSAGE);
                return message;
            }
        });
    }

    public void sendMailSendingError(Exception e) {
        Map message = new HashMap();
        message.put(Fields.TEXT, e.getMessage());
        message.put(Fields.DATE, TimeUtil.dateFormatWithSec.format(Calendar.getInstance().getTime()));
        mailQueue.convertAndSend(message, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws JMSException {
                message.setStringProperty(Fields.TYPE, Values.ERROR);
                return message;
            }
        });
    }
}
