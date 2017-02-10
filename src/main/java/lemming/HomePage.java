package lemming;

import lemming.auth.WebSession;
import lemming.character.CharacterEditPage;
import lemming.context.ContextIndexPage;
import lemming.context.ContextImportPage;
import lemming.lemma.LemmaIndexPage;
import lemming.lemmatisation.LemmatisationPage;
import lemming.pos.PosIndexPage;
import lemming.sense.SenseIndexPage;
import lemming.ui.UserBookmarkablePageLink;
import lemming.ui.page.BasePage;
import lemming.user.UserEditPage;
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
        BookmarkablePageLink<Void> lemmatisationLink = new BookmarkablePageLink<Void>("lemmatisationLink",
                LemmatisationPage.class);
        BookmarkablePageLink<Void> contextIndexLink = new BookmarkablePageLink<Void>("contextIndexLink",
                ContextIndexPage.class);
        UserBookmarkablePageLink contextImportLink = new UserBookmarkablePageLink("contextImportLink",
                ContextImportPage.class);
        BookmarkablePageLink<Void> lemmaIndexLink = new BookmarkablePageLink<Void>("lemmaIndexLink",
                LemmaIndexPage.class);
        BookmarkablePageLink<Void> senseIndexLink = new BookmarkablePageLink<Void>("senseIndexLink",
                SenseIndexPage.class);
        BookmarkablePageLink<Void> posIndexLink = new BookmarkablePageLink<Void>("posIndexLink", PosIndexPage.class);
        BookmarkablePageLink<Void> userEditLink = new BookmarkablePageLink<Void>("userEditLink", UserEditPage.class);
        UserBookmarkablePageLink characterEditPageLink = new UserBookmarkablePageLink("characterEditPageLink",
                CharacterEditPage.class);

        // check if the session is expired
        WebSession.get().checkSessionExpired(getPageClass());

        add(lemmatisationLink);
        add(contextIndexLink);
        add(contextImportLink);
        add(lemmaIndexLink);
        add(senseIndexLink);
        add(posIndexLink);
        add(userEditLink);
        add(characterEditPageLink);
    }
}
