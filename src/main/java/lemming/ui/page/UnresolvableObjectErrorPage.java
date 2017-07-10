package lemming.ui.page;

import lemming.ui.TitleLabel;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.hibernate.UnresolvableObjectException;

import lemming.HomePage;

/**
 * Displayed when an unresolvable object exception occurs.
 */
@AuthorizeInstantiation({ "SIGNED_IN" })
public class UnresolvableObjectErrorPage extends EmptyBasePage {
    /**
     * Available action types.
     */
    public enum ActionType {
        SAVE, REMOVE
    }

    /**
     * Type of action that failed due an unresolvable object.
     */
    private ActionType action;

    /*
     * Exception raised due to an unresolvable object.
     */
    private Exception exception;

    /**
     * Empty constructor which is used when a user isn’t signed in.
     */
    public UnresolvableObjectErrorPage() {
    }

    /**
     * Creates an unresolvable object error page.
     *  @param element
     *            element which failed to save or remove
     * @param exception
     */
    public UnresolvableObjectErrorPage(Object element, Exception exception) {
        this.action = ActionType.REMOVE;
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

        if (identifier != null) {
            add(new Label("identifier", identifier));
        } else {
            add(new Label("identifier", "").setVisible(false));
        }

        if (entityName != null) {
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

        if (identifier != null) {
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
