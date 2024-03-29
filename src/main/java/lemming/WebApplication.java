package lemming;

import lemming.auth.SignInPage;
import lemming.auth.WebSession;
import lemming.context.*;
import lemming.lemma.LemmaEditPage;
import lemming.lemma.LemmaIndexPage;
import lemming.lemmatisation.LemmatisationPage;
import lemming.pos.PosEditPage;
import lemming.pos.PosIndexPage;
import lemming.resource.ResourcePage;
import lemming.ui.page.AccessDeniedPage;
import lemming.ui.page.PageExpiredPage;
import lemming.user.UserEditPage;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AnnotationsRoleAuthorizationStrategy;
import org.apache.wicket.markup.html.SecurePackageResourceGuard;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.settings.ExceptionSettings;

import java.util.List;
import java.util.logging.Logger;

/**
 * A web application that does role-based authentication.
 */
public class WebApplication extends AuthenticatedWebApplication {

    private Logger logger = Logger.getLogger(WebApplication.class.getName());

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
                component -> {
                    if (component instanceof Page) {
                        if (WebSession.get().getUser() != null) {
                            throw new UnauthorizedInstantiationException(AccessDeniedPage.class);
                        } else {
                            if (component instanceof ContextEditPage || component instanceof LemmaEditPage ||
                                    component instanceof PosEditPage) {
                                component.setResponsePage(SignInPage.class);
                            } else {
                                throw new RestartResponseAtInterceptPageException(SignInPage.class);
                            }
                        }
                    }
                });
        getApplicationSettings().setAccessDeniedPage(AccessDeniedPage.class);
        getApplicationSettings().setPageExpiredErrorPage(PageExpiredPage.class);

        if (AuthenticatedWebApplication.get().getConfigurationType().equals(RuntimeConfigurationType.DEPLOYMENT)) {
            // don’t show an exception page when an unexpected exception is thrown
            getExceptionSettings().setUnexpectedExceptionDisplay(ExceptionSettings.SHOW_NO_EXCEPTION_PAGE);
        }

        if (getInitParameter("wicket.stripWicketTags").equals("true")) {
            getMarkupSettings().setStripWicketTags(true);
        }

        mountPage("/AccessDeniedPage", AccessDeniedPage.class);
        mountPage("/PageExpiredPage", PageExpiredPage.class);
        mountPage("/SignInPage", SignInPage.class);
        mountPage("/context/ContextIndexPage", ContextIndexPage.class);
        mountPage("/context/ContextEditPage", ContextEditPage.class);
        mountPage("/context/ContextImportPage", ContextImportPage.class);
        mountPage("/lemma/LemmaIndexPage", LemmaIndexPage.class);
        mountPage("/lemma/LemmaEditPage", LemmaEditPage.class);
        mountPage("/lemmatisation/LemmatisationPage", LemmatisationPage.class);
        mountPage("/pos/PosIndexPage", PosIndexPage.class);
        mountPage("/pos/PosEditPage", PosEditPage.class);
        mountPage("/resource/ResourcePage", ResourcePage.class);
        mountPage("/user/UserEditPage", UserEditPage.class);

        // TODO: remove
//        logger.info("Updating all contexts...");
//        ContextDao contextDao = new ContextDao();
//        List<Context> allContexts = contextDao.getAll();
//
//        contextDao.batchUpdate(allContexts);
//        logger.info("Done.");
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
