package angularspringapp.services.authentication;

import angularspringapp.dao.MSSQLUserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFailureHandler extends
        SimpleUrlAuthenticationFailureHandler {

    @Autowired
    MSSQLUserDAO userDAO;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
//        super.onAuthenticationFailure(request, response, exception);
        if ("BANNED".equals(exception.getMessage())) {
            userDAO.saveAudit(request.getParameter("j_username").toString(), false);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "{}");
            return;
        }
        userDAO.saveAudit(request.getParameter("j_username").toString(), false);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "{}");
    }

}