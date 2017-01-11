package lemming.api.ui.page;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import lemming.api.HomePage;

/**
 * Displayed when a Page instance cannot be found by its ID in the page stores.
 */
public class PageExpiredPage extends EmptyBasePage {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Initializes a page expired page.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Label("pageExpiredHeader", getString("PageExpiredPage.header")));
        add(new Label("pageExpiredMessage", getString("PageExpiredPage.message")));
        add(new BookmarkablePageLink<Void>("pageExpiredRedirectionLink", HomePage.class));
    }
}
