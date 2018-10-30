package lemming.context.inbound;

import lemming.ui.TitleLabel;
import lemming.ui.page.BasePage;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * A page for the verifying of inbound contexts.
 */
@AuthorizeInstantiation({"SIGNED_IN"})
public class InboundContextVerificationPage extends BasePage {
    /**
     * Model of a group of inbound contexts.
     */
    private IModel<InboundContextPackage> model;

    /**
     * Creates a context verification page.
     */
    @SuppressWarnings("unused")
    public InboundContextVerificationPage() {
    }

    /**
     * Creates a context verification page.
     *
     * @param model model of a package of inbound contexts.
     */
    public InboundContextVerificationPage(IModel<InboundContextPackage> model) {
        this.model = model;
    }

    /**
     * Called when a context verification page is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new TitleLabel(getString("InboundContextVerificationPage.header")));
        add(new Label("header", getString("InboundContextVerificationPage.header")));
        add(new InboundContextVerificationForm(model));
    }
}
