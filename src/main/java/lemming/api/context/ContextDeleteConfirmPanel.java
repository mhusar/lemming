package lemming.api.context;

import lemming.api.table.GenericDataTable;
import lemming.api.ui.panel.ModalMessagePanel;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.model.StringResourceModel;

/**
 * A panel containing a modal window dialog asking if a context shall be deleted.
 */
@AuthorizeAction(action = Action.RENDER, roles = { "SIGNED_IN" })
public class ContextDeleteConfirmPanel extends ModalMessagePanel {
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
    public ContextDeleteConfirmPanel(String id, Page responsePage) {
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
    public ContextDeleteConfirmPanel(String id, Class<? extends Page> responsePageClass) {
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
    public ContextDeleteConfirmPanel(String id, GenericDataTable<Context> dataTable) {
        super(id, DialogType.YES_NO, dataTable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitleString() {
        return getString("ContextDeleteConfirmPanel.title");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringResourceModel getMessageModel() {
        Context context = (Context) getDefaultModelObject();

        return new StringResourceModel("ContextDeleteConfirmPanel.message",(Component) this,
                getDefaultModel()).setParameters("<b>" + context.getPreceding() + context.getKeyword()
                + context.getFollowing() + "</b>");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConfirmationString() {
        return getString("ContextDeleteConfirmPanel.confirm");
    }

    /**
     * Does nothing.
     */
    @Override
    public void onCancel() {
    }

    /**
     * Removes the context of the default model.
     */
    @Override
    public void onConfirm() {
        new ContextDao().remove((Context) getDefaultModelObject());
    }
}
