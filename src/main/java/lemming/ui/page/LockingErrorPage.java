package lemming.ui.page;

import java.lang.reflect.Field;

import javax.persistence.OptimisticLockException;

import lemming.ui.TitleLabel;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.hibernate.StaleObjectStateException;

import lemming.HomePage;

/**
 * Displayed when a locking exception occurs.
 */
@AuthorizeInstantiation({ "SIGNED_IN" })
public class LockingErrorPage extends EmptyBasePage {
    /**
     * Available action types.
     */
    public enum ActionType {
        SAVE, REMOVE
    }

    /**
     * Type of action that failed due to locking.
     */
    private ActionType action;

    /**
     * Element which failed to save or remove.
     */
    private Object element;

    /*
     * Exception raised due to locking.
     */
    private Exception exception;

    /**
     * Empty constructor which is used when a user isn’t signed in.
     */
    public LockingErrorPage() {
    }

    /**
     * Creates a locking error page.
     * 
     * @param action
     *            type of action that failed due to locking
     * @param element
     *            element which failed to save or remove
     * @param exception
     *            exception raised due to locking
     */
    public LockingErrorPage(ActionType action, Object element, Exception exception) {
        this.action = action;
        this.element = element;
        this.exception = exception;

        if (!(exception instanceof OptimisticLockException || exception instanceof StaleObjectStateException)) {
            setResponsePage(HomePage.class);
        }
    }

    /**
     * Initializes a locking error page.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();

        String identifier = getIdentifier();
        String entityName = getEntityName();

        if (action == ActionType.SAVE) {
            add(new TitleLabel(getString("LockingErrorPage.saveHeader")));
            add(new Label("lockingErrorHeader", getString("LockingErrorPage.saveHeader")));
            add(new Label("lockingErrorMessage", getString("LockingErrorPage.saveMessage")));
        } else if (action == ActionType.REMOVE) {
            add(new TitleLabel(getString("LockingErrorPage.removeHeader")));
            add(new Label("lockingErrorHeader", getString("LockingErrorPage.removeHeader")));
            add(new Label("lockingErrorMessage", getString("LockingErrorPage.removeMessage")));
        }

        if (identifier instanceof String) {
            add(new Label("identifier", identifier));
        } else {
            add(new Label("identifier", "").setVisible(false));
        }

        if (entityName instanceof String) {
            add(new Label("entityName", entityName));
        } else {
            add(new Label("entityName", "").setVisible(false));
        }

        add(new Label("exceptionMessage", exception.getMessage()));
        add(new BookmarkablePageLink<Void>("lockingErrorRedirectionLink", HomePage.class));
    }

    /**
     * Extracts the identifier of an entity.
     * 
     * @return A number string or null.
     */
    private String getIdentifier() {
        Integer identifier = null;

        if (exception instanceof OptimisticLockException) {
            Field field;

            try {
                field = element.getClass().getField("id");
                identifier = field.getInt(element);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
                    ignored) {
            }
        } else if (exception instanceof StaleObjectStateException) {
            StaleObjectStateException staleObjectStateException = (StaleObjectStateException) exception;
            identifier = (Integer) staleObjectStateException.getIdentifier();
        }

        if (identifier instanceof Integer) {
            return identifier.toString();
        } else {
            return null;
        }
    }

    /**
     * Extracts the name of an entity.
     * 
     * @return A name string or null.
     */
    private String getEntityName() {
        String entityName = null;

        if (exception instanceof OptimisticLockException) {
            entityName = element.getClass().getCanonicalName();
        } else if (exception instanceof StaleObjectStateException) {
            StaleObjectStateException staleObjectStateException = (StaleObjectStateException) exception;
            entityName = staleObjectStateException.getEntityName();
        }

        return entityName;
    }
}
