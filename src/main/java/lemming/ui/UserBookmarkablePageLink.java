package lemming.ui;

import org.apache.wicket.Page;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

/**
 * A BookmarkablePageLink visible to users and admins.
 */
@AuthorizeAction(action = Action.RENDER, roles = { "USER", "ADMIN" })
public class UserBookmarkablePageLink extends BookmarkablePageLink<Void> {
    /**
     * Creates a page link.
     * 
     * @param id
     *            ID of a page link
     * @param pageClass
     *            class of page
     */
    @SuppressWarnings("SameParameterValue")
    public UserBookmarkablePageLink(String id, Class<? extends Page> pageClass) {
        super(id, pageClass);
    }
}
