package lemming.ui.page;

import lemming.ui.TitleLabel;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import lemming.HomePage;

/**
 * Displayed when a Page instance cannot be found by its ID in the page stores.
 */
@AuthorizeInstantiation({ "SIGNED_IN" })
public class PageExpiredPage extends EmptyBasePage {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Empty constructor which is used when a user isn’t signed in.
     */
    public PageExpiredPage() {
    }

    /**
     * Initializes a page expired page.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new TitleLabel(getString("PageExpiredPage.header")));
        add(new Label("pageExpiredHeader", getString("PageExpiredPage.header")));
        add(new Label("pageExpiredMessage", getString("PageExpiredPage.message")));
        add(new BookmarkablePageLink<Void>("pageExpiredRedirectionLink", HomePage.class));
    }
}
