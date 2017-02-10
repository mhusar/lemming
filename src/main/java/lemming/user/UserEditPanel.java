package lemming.user;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import lemming.auth.UserRoles;
import lemming.auth.WebSession;

/**
 * A panel containing a user edit form.
 */
public class UserEditPanel extends Panel {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a user edit panel.
     * 
     * @param id
     *            ID of the edit panel
     * @param model
     *            model of edited user
     */
    public UserEditPanel(String id, IModel<User> model) {
        super(id);
        setOutputMarkupId(true);

        // refresh User object to prevent concurrency problems
        // this happens if the edited user is the WebSession user
        new UserDao().refresh(model.getObject());
        add(new UserEditForm("userEditForm", model));
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
