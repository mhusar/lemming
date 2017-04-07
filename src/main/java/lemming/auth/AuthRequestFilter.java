package lemming.auth;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

/**
 * A request filter for authentication with the wicket session.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthRequestFilter implements ContainerRequestFilter {
    @Context
    HttpServletRequest request;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        HttpSession session = request.getSession(true);
        WebSession webSession = (WebSession) session.getAttribute("wicket:WicketFilter:session");

        if (webSession != null) {
            webSession.bind();
        }

        requestContext.setSecurityContext(new SecurityContext() {
            @Override
            public String getAuthenticationScheme() {
                return null;
            }

            @Override
            public Principal getUserPrincipal() {
                if (webSession != null) {
                    if (webSession.getUser() != null) {
                        return webSession.getUser();
                    }
                }

                return null;
            }

            @Override
            public boolean isSecure() {
                return true;
            }

            @Override
            public boolean isUserInRole(String role) {
                if (webSession != null) {
                    if (webSession.getUser() != null) {
                        return webSession.getUser().getRole().name().equals(role);
                    }
                }

                return false;
            }
        });
    }
}
