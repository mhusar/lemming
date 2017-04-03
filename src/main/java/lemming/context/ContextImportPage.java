package lemming.context;

import lemming.auth.WebSession;
import lemming.ui.TitleLabel;
import lemming.ui.page.BasePage;
import lemming.ui.panel.IndicatorOverlayPanel;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;

/**
 * A page containg a context import form.
 */
@AuthorizeInstantiation({ "SIGNED_IN" })
public class ContextImportPage extends BasePage {
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
        add(new TitleLabel(getString("ContextImportPage.header")));
        add(new Label("header", getString("ContextImportPage.header")));
        add(new ContextImportForm("contextImportForm"));
        add(new IndicatorOverlayPanel("indicatorOverlayPanel"));
    }
}
