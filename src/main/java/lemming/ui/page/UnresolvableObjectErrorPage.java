package lemming.ui.page;

import lemming.ui.TitleLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.hibernate.UnresolvableObjectException;

import lemming.HomePage;

/**
 * Displayed when an unresolvable object exception occurs.
 */
public class UnresolvableObjectErrorPage extends EmptyBasePage {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Available action types.
     */
    public enum ActionType {
        SAVE, REMOVE
    };

    /**
     * Type of action that failed due an unresolvable object.
     */
    private ActionType action;

    /*
     * Exception raised due to an unresolvable object.
     */
    private Exception exception;

    /**
     * Creates an unresolvable object error page.
     * 
     * @param action
     *            type of action that failed
     * @param element
     *            element which failed to save or remove
     * @param exception
     *            exception raised due to an unresolvable object
     */
    public UnresolvableObjectErrorPage(ActionType action, Object element,
            Exception exception) {
        this.action = action;
        this.exception = exception;

        if (!(exception instanceof UnresolvableObjectException)) {
            setResponsePage(HomePage.class);
        }
    }

    /**
     * Initializes an unresolvable object error page.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();

        String identifier = getIdentifier();
        String entityName = getEntityName();

        if (action == ActionType.SAVE) {
            add(new TitleLabel(getString("UnresolvableObjectErrorPage.saveHeader")));
            add(new Label("unresolvableObjectErrorHeader",
                    getString("UnresolvableObjectErrorPage.saveHeader")));
            add(new Label("unresolvableObjectErrorMessage",
                    getString("UnresolvableObjectErrorPage.saveMessage")));
        } else if (action == ActionType.REMOVE) {
            add(new TitleLabel(getString("UnresolvableObjectErrorPage.removeHeader")));
            add(new Label("unresolvableObjectErrorHeader",
                    getString("UnresolvableObjectErrorPage.removeHeader")));
            add(new Label("unresolvableObjectErrorMessage",
                    getString("UnresolvableObjectErrorPage.removeMessage")));
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
        add(new BookmarkablePageLink<Void>("unresolvableObjectErrorRedirectionLink", HomePage.class));
    }

    /**
     * Extracts the identifier of an entity.
     * 
     * @return A number string or null.
     */
    private String getIdentifier() {
        Integer identifier = null;

        if (exception instanceof UnresolvableObjectException) {
            UnresolvableObjectException unresolvableObjectException = (UnresolvableObjectException) exception;
            identifier = (Integer) unresolvableObjectException.getIdentifier();
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

        if (exception instanceof UnresolvableObjectException) {
            UnresolvableObjectException unresolvableObjectException = (UnresolvableObjectException) exception;
            entityName = unresolvableObjectException.getEntityName();
        }

        return entityName;
    }
}
