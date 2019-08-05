package lemming.ui.panel;

import lemming.HomePage;
import lemming.context.ContextIndexPage;
import lemming.lemma.LemmaIndexPage;
import lemming.lemmatisation.LemmatisationPage;
import lemming.pos.PosIndexPage;
import lemming.user.UserDao;
import lemming.user.UserEditPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * A panel that provides shortcuts to main parts of the app.
 */
public class HeaderPanel extends Panel {
    /**
     * Creates a header panel.
     *
     * @param activePageClass class of active page
     */
    public HeaderPanel(Class<? extends Page> activePageClass) {
        super("headerPanel");

        WebMarkupContainer homePageItem = new WebMarkupContainer("homePageItem");
        WebMarkupContainer lemmatisationItem = new WebMarkupContainer("lemmatisationItem");
        WebMarkupContainer contextIndexItem = new WebMarkupContainer("contextIndexItem");
        WebMarkupContainer lemmaIndexItem = new WebMarkupContainer("lemmaIndexItem");
        WebMarkupContainer posIndexItem = new WebMarkupContainer("posIndexItem");
        WebMarkupContainer userEditItem = new WebMarkupContainer("userEditItem");
        BookmarkablePageLink<Void> homePageLink = new BookmarkablePageLink<>("homePageLink", HomePage.class);
        BookmarkablePageLink<Void> lemmatisationLink = new BookmarkablePageLink<>("lemmatisationLink",
                LemmatisationPage.class);
        BookmarkablePageLink<Void> contextIndexLink = new BookmarkablePageLink<>("contextIndexLink",
                ContextIndexPage.class);
        BookmarkablePageLink<Void> lemmaIndexLink = new BookmarkablePageLink<>("lemmaIndexLink",
                LemmaIndexPage.class);
        BookmarkablePageLink<Void> posIndexLink = new BookmarkablePageLink<>("posIndexLink", PosIndexPage.class);
        BookmarkablePageLink<Void> userEditLink = new BookmarkablePageLink<>("userEditLink", UserEditPage.class);
        Link<Void> logoutLink = new Link<Void>("logoutLink") {
            public void onClick() {
                new UserDao().logout();
            }
        };

        homePageItem.add(homePageLink);
        lemmatisationItem.add(lemmatisationLink);
        contextIndexItem.add(contextIndexLink);
        lemmaIndexItem.add(lemmaIndexLink);
        posIndexItem.add(posIndexLink);
        userEditItem.add(userEditLink);

        add(homePageItem);
        add(lemmatisationItem);
        add(contextIndexItem);
        add(lemmaIndexItem);
        add(posIndexItem);
        add(userEditItem);
        add(logoutLink);

        if (activePageClass.equals(HomePage.class)) {
            homePageItem.add(AttributeModifier.append("class", "active"));
        } else if (activePageClass.equals(LemmatisationPage.class)) {
            lemmatisationItem.add(AttributeModifier.append("class", "active"));
        } else if (activePageClass.equals(ContextIndexPage.class)) {
            contextIndexItem.add(AttributeModifier.append("class", "active"));
        } else if (activePageClass.equals(LemmaIndexPage.class)) {
            lemmaIndexItem.add(AttributeModifier.append("class", "active"));
        } else if (activePageClass.equals(PosIndexPage.class)) {
            posIndexItem.add(AttributeModifier.append("class", "active"));
        } else if (activePageClass.equals(UserEditPage.class)) {
            userEditItem.add(AttributeModifier.append("class", "active"));
        }
    }
}
