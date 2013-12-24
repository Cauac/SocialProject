package angularspringapp.contoller;

import angularspringapp.dao.MSSQLUserDAO;
import angularspringapp.entity.Audit;
import angularspringapp.entity.User;
import angularspringapp.util.TimeUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    MSSQLUserDAO userDAO;

    @RequestMapping(value = "/getUsersList.json", method = RequestMethod.GET)
    public
    @ResponseBody
    BasicDBList getUsers() {
        List<User> users = userDAO.findAll();
        BasicDBList result = new BasicDBList();
        for (User u : users) {
            DBObject dbUser = new BasicDBObject();
            dbUser.put("username", u.getUsername());
            dbUser.put("password", u.getPassword());
            dbUser.put("banned", u.isBanned());
            result.add(dbUser);
        }
        return result;
    }

    @RequestMapping(value = "/getAuditByUser.json", method = RequestMethod.GET)
    public
    @ResponseBody
    BasicDBList getAuditByUser(String username) {
        BasicDBList result = new BasicDBList();
        List<Audit> byUser = userDAO.findByUser(username);
        for (Audit a : byUser) {
            DBObject dbAudit = new BasicDBObject("date", TimeUtil.dateFormatWithSec.format(a.getDate()));
            dbAudit.put("success", a.isSuccess());
            result.add(dbAudit);
        }
        return result;
    }

    @RequestMapping(value = "/banUser", method = RequestMethod.POST)
    public
    @ResponseBody
    void banUser(String username) {
        userDAO.banUser(username);
    }

    @RequestMapping(value = "/unbanUser", method = RequestMethod.POST)
    public
    @ResponseBody
    void unbanUser(String username) {
        userDAO.unbanUser(username);
    }
}
