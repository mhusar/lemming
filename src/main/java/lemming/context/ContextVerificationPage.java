package lemming.context;

import lemming.context.inbound.InboundContextPackage;
import lemming.ui.TitleLabel;
import lemming.ui.page.BasePage;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * A page for the verifying of inbound contexts.
 */
@AuthorizeInstantiation({"SIGNED_IN"})
public class ContextVerificationPage extends BasePage {
    /**
     * Model of a group of inbound contexts.
     */
    private IModel<InboundContextPackage> model;

    /**
     * Creates a context verification page.
     */
    @SuppressWarnings("unused")
    public ContextVerificationPage() {
    }

    /**
     * Creates a context verification page.
     *
     * @param model model of a package of inbound contexts.
     */
    public ContextVerificationPage(IModel<InboundContextPackage> model) {
        this.model = model;
    }

    /**
     * Called when a context verification page is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new TitleLabel(getString("ContextVerificationPage.header")));
        add(new Label("header", getString("ContextVerificationPage.header")));
        add(new ContextVerificationForm(model));
    }
}
