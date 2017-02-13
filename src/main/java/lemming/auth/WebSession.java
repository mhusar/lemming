package lemming.auth;

import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.feedback.DefaultCleanupFeedbackMessageFilter;
import org.apache.wicket.request.Request;

import lemming.user.IUserDao;
import lemming.user.User;
import lemming.user.UserDao;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents an authenticated web session.
 */
public class WebSession extends AuthenticatedWebSession {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * A logger named corresponding to this class.
     */
    private static final Logger logger = Logger.getLogger(WebSession.class.getName());

    /**
     * The owner of a web session.
     */
    private User user;

    /**
     * Creates a WebSession.
     * 
     * @param request
     *            the current request object
     */
    public WebSession(Request request) {
        super(request);
    }

    /**
     * Returns the web session of the active thread.
     * 
     * @return The web session of the active thread.
     */
    public static WebSession get() {
        return (WebSession) AuthenticatedWebSession.get();
    }

    /**
     * Clears all rendered and unrendered feedback messages.
     */
    public void clearFeedbackMessages() {
        getApplication().getApplicationSettings()
                .setFeedbackMessageCleanupFilter(new DefaultCleanupFeedbackMessageFilter());
    }

    /**
     * Returns the owner of a web session.
     * 
     * @return The owner of a session or null.
     */
    public User getUser() {
        if (user instanceof User) {
            return user;
        } else {
            return null;
        }
    }

    /**
     * Checks if the session is expired. Throws a RestartResponseException to
     * send the user to the sign-in page if the session user is invalid.
     * 
     * @param pageClass
     *            class of the invoking page
     * @throws RestartResponseException
     */
    public void checkSessionExpired(Class<? extends Page> pageClass) {
        Logger logger = Logger.getLogger(pageClass.getName());

        if (user instanceof User) {
            return;
        } else {
            if (isTemporary()) {
                logger.log(Level.SEVERE, "Session is expired. Redirect to sign-in page.");
                bind();
            }

            throw new RestartResponseException(SignInPage.class);
        }
    }

    /**
     * Authenticates a user by username and password.
     * 
     * @param username
     *            the username of a user
     * @param password
     *            the password of a user
     * @return True if the user could be authenticated, false if username and
     *         password don’t match or no matching user could be found.
     * @see org.apache.wicket.authroles.authentication.AuthenticatedWebSession#authenticate(java.lang.String,
     *      java.lang.String)
     */
    public boolean authenticate(String username, String password) {
        IUserDao userDao = new UserDao();

        if (userDao.isDefaultUserNeeded()) {
            try {
                userDao.createDefaultUser();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Default user creation failed");
                e.printStackTrace();
                return false;
            }
        }

        User user = userDao.findByUsername(username);

        if (user instanceof User) {
            try {
                if (userDao.authenticate(user, password)) {
                    bind();
                    this.user = user;
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Authentication failed with exception");
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    /**
     * Returns the roles of the web session user.
     * 
     * @return The roles of the web session user or null if no user id is set
     *         yet.
     */
    @Override
    public Roles getRoles() {
        Roles roles = new Roles();

        if (AuthenticatedWebSession.get().isSignedIn()) {
            roles.add(UserRoles.SIGNED_IN);
        }

        if (user instanceof User) {
            roles.add(user.getRole().name());
        }

        return roles;
    }
}
