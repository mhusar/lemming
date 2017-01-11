package lemming.api.ui.page;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import lemming.api.HomePage;

/**
 * Displayed when access to a Page instance is denied.
 */
public class AccessDeniedPage extends EmptyBasePage {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Initializes an access denied page.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Label("accessDeniedHeader",
                getString("AccessDeniedPage.header")));
        add(new Label("accessDeniedMessage",
                getString("AccessDeniedPage.message")));
        add(new BookmarkablePageLink<Void>("accessDeniedRedirectionLink",
                HomePage.class));
    }
}
