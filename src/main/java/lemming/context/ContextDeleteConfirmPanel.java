package lemming.context;

import lemming.table.GenericDataTable;
import lemming.ui.panel.ModalMessagePanel;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.model.StringResourceModel;

/**
 * A panel containing a modal window dialog asking if a context shall be deleted.
 */
@AuthorizeAction(action = Action.RENDER, roles = { "SIGNED_IN" })
public class ContextDeleteConfirmPanel extends ModalMessagePanel {
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
     * @param responsePageClass
     *            class of page loaded on confirmation
     */
    public ContextDeleteConfirmPanel(Class<? extends Page> responsePageClass) {
        super("contextDeleteConfirmPanel", responsePageClass);
    }

    /**
     * Creates a panel.
     *
     * @param dataTable
     *            data table that is refreshed
     */
    public ContextDeleteConfirmPanel(GenericDataTable<Context> dataTable) {
        super("contextDeleteConfirmPanel", DialogType.YES_NO, dataTable);
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

        return new StringResourceModel("ContextDeleteConfirmPanel.message", this,
                getDefaultModel()).setParameters(context.getPreceding() + " <i>" + context.getKeyword() +
                "</i> " + context.getFollowing());
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
     *
     * @param target target that produces an Ajax response
     */
    @Override
    public void onConfirm(AjaxRequestTarget target) {
        new ContextDao().remove((Context) getDefaultModelObject());
    }
}
