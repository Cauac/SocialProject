package angularspringapp.contoller;

import angularspringapp.dao.MongoUserDAO;
import angularspringapp.services.FlickrService;
import angularspringapp.services.UserService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Map;

@Controller
@RequestMapping("/")
public class AuthController {

    @Autowired
    FlickrService flickrService;

    @Autowired
    MongoUserDAO mongoUserDao;

    @Autowired
    UserService userService;

    @RequestMapping(value = "getUserRole", method = RequestMethod.GET)
    public
    @ResponseBody
    String check() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().iterator().next().getAuthority();
    }

    @RequestMapping(value = "getUserInfo", method = RequestMethod.GET)
    public
    @ResponseBody
    DBObject getUserInfo() {
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

    @RequestMapping(value = "/getFlickrAuthUrl", method = RequestMethod.GET)
    public
    @ResponseBody
    String getFlickAuthUrl() {
        flickrService.init();
        return flickrService.getAuthorizationUrl();
    }

    @RequestMapping(value = "/getFlickrAccessToken", method = RequestMethod.GET)
    public ModelAndView getFlickrAccessToken(String oauth_token, String oauth_verifier, HttpServletRequest request) {

        Map<String, String> token = flickrService.getAccessToken(oauth_verifier);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        userService.saveToken(auth.getName(), token, "flickr");

        return new ModelAndView("redirect:/#/tokens");
    }

    @RequestMapping(value = "/getFlickrTokenForCurrentUser", method = RequestMethod.GET)
    public
    @ResponseBody
    Object getFlickrTokenForCurrentUser(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        request.getSession().getAttribute("currentUser");
        Map userTokensInfo = userService.getUserTokensInfo(auth.getName());
        if (userTokensInfo == null) {
            return new BasicDBObject();
        }
        return userTokensInfo;
    }
}
