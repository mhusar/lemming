package lemming.pos;

import lemming.table.GenericDataTable;
import lemming.ui.panel.ModalMessagePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.model.StringResourceModel;

/**
 * A panel containing a modal window dialog stating that a part of speech could not be
 * deleted.
 */
@AuthorizeAction(action = Action.RENDER, roles = { "SIGNED_IN" })
public class PosDeleteDeniedPanel extends ModalMessagePanel {
    /**
     * Creates a panel.
     */
    public PosDeleteDeniedPanel() {
        super("posDeleteDeniedPanel", DialogType.OKAY);
    }

    /**
     * Creates a panel.
     *
     * @param dataTable
     *            data table that is refreshed
     */
    public PosDeleteDeniedPanel(GenericDataTable<Pos> dataTable) {
        super("posDeleteDeniedPanel", DialogType.OKAY, dataTable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitleString() {
        return getString("PosDeleteDeniedPanel.title");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringResourceModel getMessageModel() {
        Pos pos = (Pos) getDefaultModelObject();

        return new StringResourceModel("PosDeleteDeniedPanel.message", this,
                getDefaultModel()).setParameters("<b>" + pos.getName() + "</b>");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConfirmationString() {
        return getString("PosDeleteDeniedPanel.confirm");
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
