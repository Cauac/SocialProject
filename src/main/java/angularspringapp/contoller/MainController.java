package angularspringapp.contoller;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/")
public class MainController {

    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
    public
    @ResponseBody
    DBObject getUserInfo(HttpServletRequest req, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        DBObject userInfo = new BasicDBObject();
        String role = auth.getAuthorities().iterator().next().getAuthority();
        userInfo.put("role", role);
        if ("ROLE_ANONYMOUS".equals(role)) {
            return userInfo;
        }
        userInfo.put("username", ((User) auth.getPrincipal()).getUsername());
        return userInfo;

    }
}
