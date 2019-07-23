package lemming.context.inbound;

import lemming.ui.panel.ModalMessagePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.model.StringResourceModel;

/**
 * A panel containing a modal window dialog asking if a context shall be deleted.
 */
@AuthorizeAction(action = Action.RENDER, roles = {"SIGNED_IN"})
public class ContextAddWarningPanel extends ModalMessagePanel {
    /**
     * Creates a warning panel.
     */
    public ContextAddWarningPanel() {
        super("contextAddWarningPanel", DialogType.OKAY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitleString() {
        return getString("ContextAddWarningPanel.title");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringResourceModel getMessageModel() {
        return new StringResourceModel("ContextAddWarningPanel.message", this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConfirmationString() {
        return getString("ContextAddWarningPanel.confirm");
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

