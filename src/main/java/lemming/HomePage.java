package lemming;

import lemming.auth.WebSession;
import lemming.character.CharacterEditPage;
import lemming.context.ContextImportPage;
import lemming.context.ContextIndexPage;
import lemming.lemma.LemmaIndexPage;
import lemming.lemmatization.LemmatizationPage;
import lemming.pos.PosIndexPage;
import lemming.resource.ResourcePage;
import lemming.sense.SenseIndexPage;
import lemming.ui.TitleLabel;
import lemming.ui.UserBookmarkablePageLink;
import lemming.ui.page.BasePage;
import lemming.user.UserEditPage;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

/**
 * The home or index page of the application.
 */
@AuthorizeInstantiation("SIGNED_IN")
public class HomePage extends BasePage {
    /**
     * Initializes a home page.
     */
    public HomePage() {
        BookmarkablePageLink<Void> lemmatizationLink = new BookmarkablePageLink<>("lemmatizationLink",
                LemmatizationPage.class);
        BookmarkablePageLink<Void> contextIndexLink = new BookmarkablePageLink<>("contextIndexLink",
                ContextIndexPage.class);
        BookmarkablePageLink<Void> contextImportLink = new BookmarkablePageLink<>("contextImportLink",
                ContextImportPage.class);
        BookmarkablePageLink<Void> lemmaIndexLink = new BookmarkablePageLink<>("lemmaIndexLink",
                LemmaIndexPage.class);
        BookmarkablePageLink<Void> senseIndexLink = new BookmarkablePageLink<>("senseIndexLink",
                SenseIndexPage.class);
        BookmarkablePageLink<Void> posIndexLink = new BookmarkablePageLink<>("posIndexLink", PosIndexPage.class);
        BookmarkablePageLink<Void> resourceLink = new BookmarkablePageLink<>("resourceLink", ResourcePage.class);
        BookmarkablePageLink<Void> userEditLink = new BookmarkablePageLink<>("userEditLink", UserEditPage.class);
        UserBookmarkablePageLink characterEditPageLink = new UserBookmarkablePageLink(
                CharacterEditPage.class);

        // check if the session is expired
        WebSession.get().checkSessionExpired();

        add(lemmatizationLink);
        add(contextIndexLink);
        add(contextImportLink);
        add(lemmaIndexLink);
        add(senseIndexLink.setEnabled(false));
        add(posIndexLink);
        add(resourceLink);
        add(userEditLink);
        add(characterEditPageLink);
    }

    /**
     * Called when a home page is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new TitleLabel(getString("HomePage.overview")));
    }
}
