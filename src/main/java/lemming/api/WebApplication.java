package lemming.api;

import lemming.api.auth.SignInPage;
import lemming.api.auth.WebSession;
import lemming.api.lemma.LemmaEditPage;
import lemming.api.lemma.LemmaIndexPage;
import lemming.api.pos.PosEditPage;
import lemming.api.pos.PosIndexPage;
import lemming.api.ui.page.AccessDeniedPage;
import lemming.api.ui.page.PageExpiredPage;
import lemming.api.user.User;
import lemming.api.user.UserEditPage;
import org.apache.wicket.*;
import org.apache.wicket.authorization.IUnauthorizedComponentInstantiationListener;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AnnotationsRoleAuthorizationStrategy;
import org.apache.wicket.markup.html.SecurePackageResourceGuard;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.settings.ExceptionSettings;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * A web application that does role-based authentication.
 */
public class WebApplication extends AuthenticatedWebApplication {
    /**
     * A logger named corresponding to this class.
     */
    private static final Logger logger = Logger.getLogger(WebApplication.class.getName());

    /**
     * Provides a custom initialization for this app.
     */
    @Override
    protected void init() {
        super.init();

        SecurePackageResourceGuard packageResourceGuard = (SecurePackageResourceGuard) getResourceSettings()
                .getPackageResourceGuard();

        packageResourceGuard.addPattern("+*.css.map");
        packageResourceGuard.addPattern("+*.dtd");
        packageResourceGuard.addPattern("+*.xsd");
        getSecuritySettings().setAuthorizationStrategy(
                new AnnotationsRoleAuthorizationStrategy(this));
        getSecuritySettings().setUnauthorizedComponentInstantiationListener(
                new IUnauthorizedComponentInstantiationListener() {
                    @Override
                    public void onUnauthorizedInstantiation(Component component) {
                        if (component instanceof Page) {
                            if (WebSession.get().getUser() instanceof User) {
                                throw new UnauthorizedInstantiationException(AccessDeniedPage.class);
                            } else {
                                if (component instanceof LemmaEditPage || component instanceof LemmaIndexPage) {
                                    component.setResponsePage(SignInPage.class);
                                } else {
                                    throw new RestartResponseAtInterceptPageException(SignInPage.class);
                                }
                            }
                        }
                    }
                });
        getApplicationSettings().setAccessDeniedPage(AccessDeniedPage.class);
        getApplicationSettings().setPageExpiredErrorPage(PageExpiredPage.class);

        /*
         * Disable page recreation for expired pages this should help to prevent
         * problems with Jetty (see symptoms in Wicket #5390). Also set a random
         * attribute for every request to prevent further problems. See Wicket
         * mailing list: "How could refreshing a page cause future onClick()
         * listeners to malfunction?" (May 14, 2015).
         */
        getPageSettings().setRecreateBookmarkablePagesAfterExpiry(false);
        getRequestCycleListeners().add(new AbstractRequestCycleListener() {
            @Override
            public void onEndRequest(RequestCycle cycle) {
                Session.get().setAttribute("randomAttribute", UUID.randomUUID().toString());
            }
        });

        if (AuthenticatedWebApplication.get().getConfigurationType().equals(RuntimeConfigurationType.DEPLOYMENT)) {
            // don’t show an exception page when an unexpected exception is thrown
            getExceptionSettings().setUnexpectedExceptionDisplay(ExceptionSettings.SHOW_NO_EXCEPTION_PAGE);
            // strip wicket tags
            getMarkupSettings().setStripWicketTags(true);
        }

        mountPage("/AccessDeniedPage", AccessDeniedPage.class);
        mountPage("/PageExpiredPage", PageExpiredPage.class);
        mountPage("/SignInPage", SignInPage.class);
        mountPage("/lemma/LemmaIndexPage", LemmaIndexPage.class);
        mountPage("/lemma/LemmaEditPage", LemmaEditPage.class);
        mountPage("/pos/PosIndexPage", PosIndexPage.class);
        mountPage("/pos/PosEditPage", PosEditPage.class);
        mountPage("/user/UserEditPage", UserEditPage.class);
    }

    /**
     * Creates a home page class for this application.
     * 
     * @return A home page class.
     */
    @Override
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }

    /**
     * Creates a sign-in page class for this application.
     * 
     * @return A sign-in page class.
     */
    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return SignInPage.class;
    }

    /**
     * Creates a web session class for this application.
     * 
     * @return A web session class.
     */
    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return WebSession.class;
    }
}