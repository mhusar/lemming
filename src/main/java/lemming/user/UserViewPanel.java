package lemming.user;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import lemming.auth.UserRoles;
import lemming.auth.WebSession;
import lemming.ui.panel.FeedbackPanel;

/**
 * A panel with a view displaying some users.
 */
@AuthorizeAction(action = "RENDER", roles = { UserRoles.ADMIN })
public class UserViewPanel extends Panel {
    /**
     * Creates a user list panel.
     *
     * @param model
     *            model of selected user
     */
    public UserViewPanel(IModel<User> model) {
        super("userViewPanel");
        setOutputMarkupId(true);
        add(new UserView(model));
    }

    /**
     * A view displaying users.
     */
    public static class UserView extends RefreshingView<User> {
        /**
         * Model of the selected user.
         */
        private IModel<User> selectedUserModel;

        /**
         * Creates a user view.
         *
         * @param model
         *            model of selected user
         */
        public UserView(IModel<User> model) {
            super("userView");
            selectedUserModel = model;
        }

        /**
         * Sets the selected user.
         * 
         * @param model
         *            user model of the selected user.
         */
        @SuppressWarnings("unchecked")
        private void setSelectedUser(IModel<User> model) {
            selectedUserModel = model;

            for (Component component : this) {
                Item<User> item = (Item<User>) component;

                if (item.getModelObject().equals(selectedUserModel.getObject())) {
                    item.get("userLink").add(AttributeModifier.replace("class", "list-group-item active"));
                } else {
                    if (item.getModelObject().getEnabled()) {
                        item.get("userLink").add(AttributeModifier.replace("class", "list-group-item"));
                    } else {
                        item.get("userLink").add(AttributeModifier.replace("class", "list-group-item disabled"));
                    }
                }
            }
        }

        /**
         * Populates view items with components.
         * 
         * @param item
         *            view item that is populated
         */
        @Override
        protected void populateItem(Item<User> item) {
            final AjaxLink<User> userLink = new UserLink(item.getModel());

            if (item.getModelObject().equals(selectedUserModel.getObject())) {
                userLink.add(AttributeModifier.replace("class", "list-group-item active"));
            } else {
                if (item.getModelObject().getEnabled()) {
                    userLink.add(AttributeModifier.replace("class", "list-group-item"));
                } else {
                    userLink.add(AttributeModifier.replace("class", "list-group-item disabled"));
                }
            }

            userLink.add(new Label("userLinkText", item.getModelObject().getRealName()));
            item.add(userLink);
        }

        /**
         * Returns an iterator which iterates over item models.
         * 
         * @return An iterator which iterates over item models.
         */
        @Override
        protected Iterator<IModel<User>> getItemModels() {
            List<IModel<User>> userModels = new ArrayList<>();

            for (User user : new UserDao().getAll()) {
                userModels.add(new Model<>(user));
            }

            return userModels.iterator();
        }

        /**
         * An Ajax link that sets the selected user.
         */
        private final class UserLink extends AjaxLink<User> {
            /**
             * Creates a user link.
             *
             * @param model
             *            model of a user
             */
            private UserLink(IModel<User> model) {
                super("userLink", model);
            }

            /**
             * Called when a link is clicked.
             * 
             * @param target
             *            target that produces an Ajax response
             */
            @Override
            public void onClick(AjaxRequestTarget target) {
                UserView userView = findParent(UserView.class);
                UserEditPage userEditPage = findParent(UserEditPage.class);
                UserEditPanel userEditPanel = (UserEditPanel) userEditPage.get("userEditPanel");
                MarkupContainer feedbackPanel = (FeedbackPanel) userEditPage.get("feedbackPanel");
                Component newUserEditForm = new UserEditForm(
                        new CompoundPropertyModel<>(getModelObject()));

                userView.setSelectedUser(getModel());
                target.addChildren(userView, AjaxLink.class);
                target.add(userEditPanel.addOrReplace(newUserEditForm));
                target.focusComponent(newUserEditForm.get("realName"));

                // clear feedback panel
                WebSession.get().clearFeedbackMessages();
                target.add(feedbackPanel);
            }
        }
    }
}
