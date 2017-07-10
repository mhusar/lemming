package lemming.user;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.model.StringResourceModel;

import lemming.ui.panel.ModalMessagePanel;

/**
 * A panel containing a modal window dialog stating that a user could not be
 * deleted.
 */
@AuthorizeAction(action = Action.RENDER, roles = { "ADMIN" })
public class UserDeleteDeniedPanel extends ModalMessagePanel {
    /**
     * Creates a panel.
     *
     */
    public UserDeleteDeniedPanel() {
        super("userDeleteDeniedPanel", DialogType.OKAY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitleString() {
        return getString("UserDeleteDeniedPanel.title");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringResourceModel getMessageModel() {
        User user = (User) getDefaultModelObject();

        return new StringResourceModel("UserDeleteDeniedPanel.message", this,
                getDefaultModel()).setParameters("<b>" + user.getRealName() + "</b>");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConfirmationString() {
        return getString("UserDeleteDeniedPanel.confirm");
    }

    /**
     * Does nothing.
     */
    @Override
    public void onCancel() {
    }

    /**
     * Does nothing.
     *
     * @param target target that produces an Ajax response
     */
    @Override
    public void onConfirm(AjaxRequestTarget target) {
    }
}
