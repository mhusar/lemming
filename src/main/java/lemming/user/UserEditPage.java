package lemming.user;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import lemming.auth.UserRoles;
import lemming.auth.WebSession;
import lemming.ui.page.BasePage;
import lemming.ui.panel.FeedbackPanel;

/**
 * A page containing a user list and a user edit form.
 */
@AuthorizeInstantiation("SIGNED_IN")
public class UserEditPage extends BasePage {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Model of the edited user object.
     */
    private CompoundPropertyModel<User> userModel;

    /**
     * Creates a new user edit page.
     */
    public UserEditPage() {
        User sessionUser = WebSession.get().getUser();

        // check if the session is expired
        WebSession.get().checkSessionExpired(getPageClass());
        userModel = new CompoundPropertyModel<User>(sessionUser);
    }

    /**
     * Called when a user edit page is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        MarkupContainer feedbackPanel = new FeedbackPanel("feedbackPanel");

        add(new UserDeleteDeniedPanel("userDeleteDeniedPanel"));
        add(feedbackPanel);
        add(new UserEditPanel("userEditPanel", userModel));
        add(new UserViewPanel("userViewPanel", userModel));
        add(new AddUserButton("addUserButton"));
        feedbackPanel.setOutputMarkupId(true);
    }

    /**
     * Returns markup variations based on user roles.
     * 
     * @return An identifier for a markup variation.
     */
    @Override
    public String getVariation() {
        User sessionUser = WebSession.get().getUser();

        if (!(sessionUser instanceof User)) {
            return "user";
        }

        if (sessionUser.getRole().equals(UserRoles.Role.ADMIN)) {
            return super.getVariation();
        } else {
            return "user";
        }
    }

    /**
     * A button which starts the creation of a new user.
     */
    @AuthorizeAction(action = "RENDER", roles = { UserRoles.ADMIN })
    private final class AddUserButton extends AjaxLink<Void> {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a new add user button.
         * 
         * @param id
         *            ID of a button
         */
        private AddUserButton(String id) {
            super(id);
        }

        /**
         * Called on button click.
         * 
         * @param target
         *            target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            MarkupContainer userEditPage = (UserEditPage) findParent(UserEditPage.class);
            MarkupContainer userViewPanel = (UserViewPanel) userEditPage.get("userViewPanel");
            MarkupContainer feedbackPanel = (FeedbackPanel) userEditPage.get("feedbackPanel");
            Component userView = new UserViewPanel.UserView("userView", new Model<User>(new User()));
            MarkupContainer userEditPanel = (UserEditPanel) userEditPage.get("userEditPanel");
            Component userEditForm = new UserEditForm("userEditForm", new CompoundPropertyModel<User>(new User()));

            target.add(userViewPanel.addOrReplace(userView));
            target.add(userEditPanel.addOrReplace(userEditForm));
            target.focusComponent(userEditForm.get("realName"));

            // clear feedback panel
            WebSession.get().clearFeedbackMessages();
            target.add(feedbackPanel);
        }
    }
}
