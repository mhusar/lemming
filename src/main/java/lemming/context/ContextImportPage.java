package lemming.context;

import lemming.auth.WebSession;
import lemming.ui.page.BasePage;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;

/**
 * A page containg a context import form.
 */
@AuthorizeInstantiation({ "STUDENT", "USER", "ADMIN" })
public class ContextImportPage extends BasePage {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a context import page.
     */
    public ContextImportPage() {
        // check if the session is expired
        WebSession.get().checkSessionExpired();
    }

    /**
     * Called when a context import page is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new Label("header", getString("ContextImportPage.header")));
        add(new ContextImportForm("contextImportForm"));
    }
}
