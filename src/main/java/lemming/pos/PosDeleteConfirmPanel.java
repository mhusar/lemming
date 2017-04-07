package lemming.pos;

import lemming.table.GenericDataTable;
import lemming.ui.panel.ModalMessagePanel;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.model.StringResourceModel;

/**
 * A panel containing a modal window dialog asking if a part of speech shall be deleted.
 */
@AuthorizeAction(action = Action.RENDER, roles = { "SIGNED_IN" })
public class PosDeleteConfirmPanel extends ModalMessagePanel {
    /**
     * Creates a panel.
     *
     * @param responsePage
     *            page loaded on confirmation
     */
    @SuppressWarnings("unused")
    public PosDeleteConfirmPanel(Page responsePage) {
        super("posDeleteConfirmPanel", DialogType.YES_NO, responsePage);
    }

    /**
     * Creates a panel.
     *
     * @param responsePageClass
     *            class of page loaded on confirmation
     */
    public PosDeleteConfirmPanel(Class<? extends Page> responsePageClass) {
        super("posDeleteConfirmPanel", DialogType.YES_NO, responsePageClass);
    }

    /**
     * Creates a panel.
     *
     * @param dataTable
     *            data table that is refreshed
     */
    public PosDeleteConfirmPanel(GenericDataTable<Pos> dataTable) {
        super("posDeleteConfirmPanel", DialogType.YES_NO, dataTable);
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

        return new StringResourceModel("PosDeleteConfirmPanel.message", this,
                getDefaultModel()).setParameters("<b>" + pos.getName() + "</b>");
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
     *
     * @param target target that produces an Ajax response
     */
    @Override
    public void onConfirm(AjaxRequestTarget target) {
        new PosDao().remove((Pos) getDefaultModelObject());
    }
}
