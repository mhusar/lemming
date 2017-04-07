package lemming.auth;

import lemming.HomePage;
import lemming.ui.TitleLabel;
import lemming.ui.page.EmptyBasePage;
import lemming.ui.panel.FeedbackPanel;
import lemming.user.User;
import lemming.user.UserDao;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The sign-in page of the application.
 */
public class SignInPage extends EmptyBasePage {
    /**
     * A logger named corresponding to this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(SignInPage.class);
    /**
     * Username string.
     */
    @SuppressWarnings("unused")
    private String username;
    /**
     * Password string.
     */
    @SuppressWarnings("unused")
    private String password;

    /**
     * Creates a sign-in page.
     */
    public SignInPage() {
        add(new FeedbackPanel());
        add(new SignInForm().setDefaultModel(new CompoundPropertyModel<>(this)));
        setStatelessHint(true);
    }

    /**
     * Called when a sign in page is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new TitleLabel(getString("SignInPage.header")));
    }

    /**
     * Returns the username string.
     *
     * @return A string.
     */
    private String getUsername() {
        return username;
    }

    /**
     * Returns the password string.
     *
     * @return A string.
     */
    private String getPassword() {
        return password;
    }

    /**
     * A stateless form which authenticates a user.
     */
    private class SignInForm extends StatelessForm {
        /**
         * Creates a sign-in form.
         */
        public SignInForm() {
            super("signInForm");
            add(new TextField<String>("username"));
            add(new PasswordTextField("password"));
        }

        /**
         * Submits the form input to authenticate a user.
         */
        @Override
        protected void onSubmit() {
            if (WebSession.get().isTemporary()) {
                WebSession.get().bind();
            }

            if (WebSession.get().signIn(getUsername(), getPassword())) {
                continueToOriginalDestination();
                setResponsePage(HomePage.class);
            } else {
                User matchingUser = new UserDao().findByUsername(getUsername());

                if (matchingUser != null) {
                    if (matchingUser.getEnabled()) {
                        error(getString("SignInPage.passwordWrongMessage"));
                        logger.info("Password wrong for user " + getUsername() + ".");
                    } else {
                        error(getString("SignInPage.userNotEnabledMessage"));
                        logger.info("User not enabled: " + getUsername() + ".");
                    }
                } else {
                    error(getString("SignInPage.userUnknownMessage"));
                    logger.info("User unknown: " + getUsername() + ".");
                }
            }
        }
    }
}
