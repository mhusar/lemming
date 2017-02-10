package lemming.context;

import lemming.table.GenericDataTable;
import lemming.ui.panel.ModalMessagePanel;
import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.model.StringResourceModel;

/**
 * A panel containing a modal window dialog stating that a context could not be deleted.
 */
@AuthorizeAction(action = Action.RENDER, roles = { "SIGNED_IN" })
public class ContextDeleteDeniedPanel extends ModalMessagePanel {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a panel.
     * 
     * @param id
     *            ID of the panel
     */
    public ContextDeleteDeniedPanel(String id) {
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
    public ContextDeleteDeniedPanel(String id, GenericDataTable<Context> dataTable) {
        super(id, DialogType.OKAY, dataTable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitleString() {
        return getString("ContextDeleteDeniedPanel.title");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringResourceModel getMessageModel() {
        Context context = (Context) getDefaultModelObject();

        return new StringResourceModel("ContextDeleteDeniedPanel.message",
                (Component) this, getDefaultModel()).setParameters("<b>" + context.getKeyword() + "</b>");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConfirmationString() {
        return getString("ContextDeleteDeniedPanel.confirm");
    }

    /**
     * Does nothing.
     */
    @Override
    public void onCancel() {
    }

    /**
     * Does nothing.
     */
    @Override
    public void onConfirm() {
    }
}
