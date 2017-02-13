package lemming.ui.panel;

import lemming.context.ContextIndexPage;
import lemming.lemma.LemmaIndexPage;
import lemming.lemmatisation.LemmatisationPage;
import lemming.pos.PosIndexPage;
import lemming.sense.SenseIndexPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

import lemming.HomePage;
import lemming.user.UserDao;
import lemming.user.UserEditPage;

/**
 * A panel that provides shortcuts to main parts of the app.
 */
public class HeaderPanel extends Panel {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new header panel.
     * 
     * @param id
     *            ID of a panel
     * @param activePageClass
     *            class of active page
     */
    public HeaderPanel(String id, Class<? extends Page> activePageClass) {
        super(id);

        WebMarkupContainer homePageItem = new WebMarkupContainer("homePageItem");
        WebMarkupContainer lemmatisationItem = new WebMarkupContainer("lemmatisationItem");
        WebMarkupContainer contextIndexItem = new WebMarkupContainer("contextIndexItem");
        WebMarkupContainer lemmaIndexItem = new WebMarkupContainer("lemmaIndexItem");
        WebMarkupContainer posIndexItem = new WebMarkupContainer("posIndexItem");
        WebMarkupContainer senseIndexItem = new WebMarkupContainer("senseIndexItem");
        WebMarkupContainer userEditItem = new WebMarkupContainer("userEditItem");
        BookmarkablePageLink<Void> homePageLink = new BookmarkablePageLink<Void>("homePageLink", HomePage.class);
        BookmarkablePageLink<Void> lemmatisationLink = new BookmarkablePageLink<Void>("lemmatisationLink",
                LemmatisationPage.class);
        BookmarkablePageLink<Void> contextIndexLink = new BookmarkablePageLink<Void>("contextIndexLink",
                ContextIndexPage.class);
        BookmarkablePageLink<Void> lemmaIndexLink = new BookmarkablePageLink<Void>("lemmaIndexLink",
                LemmaIndexPage.class);
        BookmarkablePageLink<Void> posIndexLink = new BookmarkablePageLink<Void>("posIndexLink", PosIndexPage.class);
        BookmarkablePageLink<Void> senseIndexLink = new BookmarkablePageLink<Void>("senseIndexLink",
                SenseIndexPage.class);
        BookmarkablePageLink<Void> userEditLink = new BookmarkablePageLink<Void>("userEditLink", UserEditPage.class);
        Link<Void> logoutLink = new Link<Void>("logoutLink") {
            private static final long serialVersionUID = 1L;

            public void onClick() {
                new UserDao().logout();
            }
        };

        homePageItem.add(homePageLink);
        lemmatisationItem.add(lemmatisationLink);
        contextIndexItem.add(contextIndexLink);
        lemmaIndexItem.add(lemmaIndexLink);
        posIndexItem.add(posIndexLink);
        senseIndexItem.add(senseIndexLink);
        userEditItem.add(userEditLink);

        add(homePageItem);
        add(lemmatisationItem);
        add(contextIndexItem);
        add(lemmaIndexItem);
        add(posIndexItem);
        add(senseIndexItem);
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
        } else if (activePageClass.equals(SenseIndexPage.class)) {
            senseIndexItem.add(AttributeModifier.append("class", "active"));
        } else if (activePageClass.equals(PosIndexPage.class)) {
            posIndexItem.add(AttributeModifier.append("class", "active"));
        } else if (activePageClass.equals(UserEditPage.class)) {
            userEditItem.add(AttributeModifier.append("class", "active"));
        }
    }
}
