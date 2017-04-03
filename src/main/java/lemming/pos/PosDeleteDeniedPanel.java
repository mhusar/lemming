package lemming.pos;

import lemming.table.GenericDataTable;
import lemming.ui.panel.ModalMessagePanel;
import org.apache.wicket.Component;
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
     * 
     * @param id
     *            ID of the panel
     */
    public PosDeleteDeniedPanel(String id) {
        super(id, DialogType.OKAY);
    }

    /**
     * Creates a panel.
     * 
     * @param id
     *            ID of the panel
     * @param dataTable
     *            data table that is refreshed
     */
    public PosDeleteDeniedPanel(String id, GenericDataTable<Pos> dataTable) {
        super(id, DialogType.OKAY, dataTable);
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

        return new StringResourceModel("PosDeleteDeniedPanel.message",
                (Component) this, getDefaultModel()).setParameters("<b>" + pos.getName() + "</b>");
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
