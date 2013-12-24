package angularspringapp.services.jms.receiver;

import javax.jms.*;

import angularspringapp.services.jms.JMSMessageSender;
import angularspringapp.services.jms.MessageDetails.*;
import angularspringapp.services.mail.MailService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.mail.MailException;

public class MailReceiver implements SessionAwareMessageListener {

    private String applicationMail;

    private int deliveryMaxRepeat;

    private static final Logger logger = Logger.getLogger(MailReceiver.class);

    @Autowired
    MailService mailService;

    @Autowired
    JMSMessageSender jmsMessageSender;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        if (message instanceof MapMessage) {
            MapMessage mapMessage = (MapMessage) message;
            try {
                String mail = mapMessage.getString(Fields.MAIL);
                String login = mapMessage.getString(Fields.USERNAME);
                String password = mapMessage.getString(Fields.PASSWORD);
                String text = String.format("Your login %s , your password %s", login, password);
                mailService.sendMail(applicationMail, mail, "Restore Password", text);
                session.close();
            } catch (JMSException e) {
                exceptionProcessing(message, session, e);
            } catch (MailException e) {
                exceptionProcessing(message, session, e);
            }
        }
    }

    private void exceptionProcessing(Message message, Session session, Exception e) throws JMSException {
        logger.error(e.getMessage());
        int deliveryCount = message.getIntProperty("JMSXDeliveryCount");
        if (deliveryCount > deliveryMaxRepeat) {
            jmsMessageSender.sendMailSendingError(e);
        } else {
            session.rollback();
        }
    }

    public void setApplicationMail(String applicationMail) {
        this.applicationMail = applicationMail;
    }

    public void setDeliveryMaxRepeat(int deliveryMaxRepeat) {
        this.deliveryMaxRepeat = deliveryMaxRepeat;
    }
}
