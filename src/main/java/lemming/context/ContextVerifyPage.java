package lemming.context;

import lemming.context.verfication.UnverifiedContextOverview;
import lemming.ui.page.BasePage;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.IModel;

/**
 * A page which assists user in verifying contexts.
 */
@AuthorizeInstantiation({ "SIGNED_IN" })
public class ContextVerifyPage extends BasePage {
    /**
     * An unverfied context overview model.
     */
    private IModel<UnverifiedContextOverview> model;

    /**
     * Private constructor.
     */
    private ContextVerifyPage() {
        // does nothing
    }

    /**
     * Creates a context verify page
     *
     * @param model model of the page
     */
    public ContextVerifyPage(IModel<UnverifiedContextOverview> model) {
        this.model = model;
    }
}
