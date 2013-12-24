package angularspringapp.contoller;

import angularspringapp.dao.MongoUserDAO;
import angularspringapp.services.jms.JMSMessageSender;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class RestorePasswordController {

    @Autowired
    MongoUserDAO userDAO;

//    @Autowired
//    JMSMessageSender messageSender;

    @RequestMapping(value = "sendCredentials")
    public void sendCredentials(String mail, HttpServletResponse response) throws IOException {
//        DBObject user = userDAO.readByMail(mail);
//        if (user == null) {
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "{}");
//        }
//        messageSender.sendRestorePasswordMessage(mail, user.get("_id").toString(), user.get("password").toString());
    }
}
