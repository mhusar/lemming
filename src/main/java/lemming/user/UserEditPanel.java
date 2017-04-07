package lemming.user;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import lemming.auth.UserRoles;
import lemming.auth.WebSession;

/**
 * A panel containing a user edit form.
 */
class UserEditPanel extends Panel {
    /**
     * Creates a user edit panel.
     *
     * @param model model of edited user
     */
    public UserEditPanel(IModel<User> model) {
        super("userEditPanel");
        setOutputMarkupId(true);

        // refresh User object to prevent concurrency problems
        // this happens if the edited user is the WebSession user
        new UserDao().refresh(model.getObject());
        add(new UserEditForm(model));
    }

    /**
     * Returns markup variations based on user roles.
     *
     * @return An identifier for a markup variation.
     */
    @Override
    public String getVariation() {
        if (WebSession.get().getUser().getRole().equals(UserRoles.Role.ADMIN)) {
            return super.getVariation();
        } else {
            return "user";
        }
    }
}
