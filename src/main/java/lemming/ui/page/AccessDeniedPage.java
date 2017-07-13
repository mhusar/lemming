package lemming.ui.page;

import lemming.HomePage;
import lemming.ui.TitleLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

/**
 * Displayed when access to a Page instance is denied.
 */
public class AccessDeniedPage extends EmptyBasePage {
    /**
     * Initializes an access denied page.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new TitleLabel(getString("AccessDeniedPage.header")));
        add(new Label("accessDeniedHeader", getString("AccessDeniedPage.header")));
        add(new Label("accessDeniedMessage", getString("AccessDeniedPage.message")));
        add(new BookmarkablePageLink<Void>("accessDeniedRedirectionLink", HomePage.class));
    }
}
