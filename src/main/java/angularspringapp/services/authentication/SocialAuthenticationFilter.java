package angularspringapp.services.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class SocialAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    protected SocialAuthenticationFilter() {
        super("/j_spring_service_security_check");
    }

    @Autowired
    SocialUserProvider socialUserProvider;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
//        if (!request.getMethod().equals("POST")) {
//            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
//        }

        String id = request.getParameter("profile_id");
        String serviceName = request.getParameter("service_name");

        if (id == null) {
            id = "";
        }

        if (serviceName == null) {
            serviceName = "";
        }

        id = id.trim();
        serviceName = serviceName.trim();

        return socialUserProvider.getUserByServiceProfileId(serviceName, id);
    }
}
