package lemming.api;

import lemming.api.auth.WebSession;
import lemming.api.character.CharacterEditPage;
import lemming.api.context.ContextIndexPage;
import lemming.api.context.ContextImportPage;
import lemming.api.lemma.LemmaIndexPage;
import lemming.api.pos.PosIndexPage;
import lemming.api.ui.UserBookmarkablePageLink;
import lemming.api.ui.page.BasePage;
import lemming.api.user.UserEditPage;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import java.util.logging.Logger;

/**
 * The home or index page of the application.
 */
@AuthorizeInstantiation("SIGNED_IN")
public class HomePage extends BasePage {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * A logger named corresponding to this class.
     */
    private static final Logger logger = Logger.getLogger(HomePage.class.getName());

    /**
     * Initializes a home page.
     */
    public HomePage() {
        BookmarkablePageLink<Void> contextIndexLink = new BookmarkablePageLink<Void>("contextIndexLink",
                ContextIndexPage.class);
        UserBookmarkablePageLink contextImportLink = new UserBookmarkablePageLink("contextImportLink",
                ContextImportPage.class);
        BookmarkablePageLink<Void> lemmaIndexLink = new BookmarkablePageLink<Void>("lemmaIndexLink",
                LemmaIndexPage.class);
        BookmarkablePageLink<Void> posIndexLink = new BookmarkablePageLink<Void>("posIndexLink", PosIndexPage.class);
        BookmarkablePageLink<Void> userEditLink = new BookmarkablePageLink<Void>("userEditLink", UserEditPage.class);
        UserBookmarkablePageLink characterEditPageLink = new UserBookmarkablePageLink("characterEditPageLink",
                CharacterEditPage.class);

        // check if the session is expired
        WebSession.get().checkSessionExpired(getPageClass());

        add(contextIndexLink);
        add(contextImportLink);
        add(lemmaIndexLink);
        add(posIndexLink);
        add(userEditLink);
        add(characterEditPageLink);
    }
}
