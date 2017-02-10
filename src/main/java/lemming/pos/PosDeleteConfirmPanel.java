package lemming.pos;

import lemming.table.GenericDataTable;
import lemming.ui.panel.ModalMessagePanel;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.model.StringResourceModel;

/**
 * A panel containing a modal window dialog asking if a part of speech shall be deleted.
 */
@AuthorizeAction(action = Action.RENDER, roles = { "SIGNED_IN" })
public class PosDeleteConfirmPanel extends ModalMessagePanel {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a panel.
     * 
     * @param id
     *            ID of the panel
     * @param responsePage
     *            page loaded on confirmation
     */
    public PosDeleteConfirmPanel(String id, Page responsePage) {
        super(id, DialogType.YES_NO, responsePage);
    }

    /**
     * Creates a panel.
     * 
     * @param id
     *            ID of the panel
     * @param responsePageClass
     *            class of page loaded on confirmation
     */
    public PosDeleteConfirmPanel(String id, Class<? extends Page> responsePageClass) {
        super(id, DialogType.YES_NO, responsePageClass);
    }

    /**
     * Creates a panel.
     * 
     * @param id
     *            ID of the panel
     * @param dataTable
     *            data table that is refreshed
     */
    public PosDeleteConfirmPanel(String id, GenericDataTable<Pos> dataTable) {
        super(id, DialogType.YES_NO, dataTable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitleString() {
        return getString("PosDeleteConfirmPanel.title");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringResourceModel getMessageModel() {
        Pos pos = (Pos) getDefaultModelObject();

        return new StringResourceModel("PosDeleteConfirmPanel.message",
                (Component) this, getDefaultModel()).setParameters("<b>" + pos.getName() + "</b>");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConfirmationString() {
        return getString("PosDeleteConfirmPanel.confirm");
    }

    /**
     * Does nothing.
     */
    @Override
    public void onCancel() {
    }

    /**
     * Removes the part of speech of the default model.
     */
    @Override
    public void onConfirm() {
        new PosDao().remove((Pos) getDefaultModelObject());
    }
}
