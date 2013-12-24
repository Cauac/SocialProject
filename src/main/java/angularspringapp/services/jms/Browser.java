package angularspringapp.services.jms;

import javax.jms.*;
import java.util.*;

public class Browser {

    private ConnectionFactory connectionFactory;


    public Collection<Map> viewErrors() {
        Collection<Map> result = new ArrayList<Map>();
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, 1);
            QueueBrowser browser = session.createBrowser(session.createQueue("MailQueue"), "type='ERROR'");
            Enumeration enumeration = browser.getEnumeration();
            while (enumeration.hasMoreElements()) {
                MapMessage message = (MapMessage) enumeration.nextElement();
                Map map = new HashMap();
                map.put("message", message.getString(MessageDetails.Fields.TEXT));
                map.put("date", message.getString(MessageDetails.Fields.DATE));
                result.add(map);
            }


        } catch (JMSException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            try {
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return result;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
}
