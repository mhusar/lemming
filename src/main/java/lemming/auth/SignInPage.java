package lemming.auth;

import lemming.HomePage;
import lemming.ui.page.EmptyBasePage;
import lemming.ui.panel.FeedbackPanel;
import lemming.user.User;
import lemming.user.UserDao;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.value.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The sign-in page of the application.
 */
public class SignInPage extends EmptyBasePage {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * A logger named corresponding to this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(SignInPage.class);

    /**
     * Creates a sign-in page.
     */
    public SignInPage() {
        add(new FeedbackPanel("feedbackPanel"));
        add(new SignInForm("signInForm"));
    }

    /**
     * A stateless form which authenticates a user.
     */
    private class SignInForm extends Form<String> {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Constant used for username property.
         */
        private static final String USERNAME = "username";

        /**
         * Constant used for password property.
         */
        private static final String PASSWORD = "password";

        /**
         * Saves the input of text and password fields.
         */
        private final ValueMap properties = new ValueMap();

        /**
         * Creates a sign-in form.
         * 
         * @param id
         *            ID of a sign-in form
         */
        public SignInForm(String id) {
            super(id);
            add(new TextField<String>("username", new PropertyModel<String>(properties, USERNAME), String.class));
            add(new PasswordTextField("password", new PropertyModel<String>(properties, PASSWORD)));
        }

        /**
         * Submits the form input to authenticate a user.
         */
        @Override
        protected void onSubmit() {
            if (WebSession.get().signIn(properties.getString(USERNAME), properties.getString(PASSWORD))) {
                continueToOriginalDestination();
                setResponsePage(HomePage.class);
            } else {
                User matchingUser = new UserDao().findByUsername(properties.getString(USERNAME));

                if (matchingUser instanceof User) {
                    if (matchingUser.getEnabled()) {
                        error(getString("SignInPage.passwordWrongMessage"));
                        logger.info("Password wrong for user " + properties.getString(USERNAME) + ".");
                    } else {
                        error(getString("SignInPage.userNotEnabledMessage"));
                        logger.info("User not enabled: " + properties.getString(USERNAME) + ".");
                    }
                } else {
                    error(getString("SignInPage.userUnknownMessage"));
                    logger.info("User unknown: " + properties.getString(USERNAME) + ".");
                }
            }
        }
    }
}
