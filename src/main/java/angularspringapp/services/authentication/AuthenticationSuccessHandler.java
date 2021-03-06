package angularspringapp.services.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import angularspringapp.dao.MSSQLUserDAO;
import angularspringapp.services.UserService;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    UserService userService;

    @Autowired
    MSSQLUserDAO auditDAO;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String username = ((User) authentication.getPrincipal()).getUsername();
        DBObject user = userService.getUser(username);
        request.getSession().setAttribute("currentUser", user);
        auditDAO.saveAudit(username, true);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
