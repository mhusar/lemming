package lemming.lemma;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.model.StringResourceModel;

import lemming.table.GenericDataTable;
import lemming.ui.panel.ModalMessagePanel;

/**
 * A panel containing a modal window dialog asking if a lemma shall be deleted.
 */
@AuthorizeAction(action = Action.RENDER, roles = { "SIGNED_IN" })
public class LemmaDeleteConfirmPanel extends ModalMessagePanel {
    /**
     * Creates a panel.
     * 
     * @param id
     *            ID of the panel
     * @param responsePage
     *            page loaded on confirmation
     */
    public LemmaDeleteConfirmPanel(String id, Page responsePage) {
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
    public LemmaDeleteConfirmPanel(String id, Class<? extends Page> responsePageClass) {
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
    public LemmaDeleteConfirmPanel(String id, GenericDataTable<Lemma> dataTable) {
        super(id, DialogType.YES_NO, dataTable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitleString() {
        return getString("LemmaDeleteConfirmPanel.title");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringResourceModel getMessageModel() {
        Lemma lemma = (Lemma) getDefaultModelObject();

        return new StringResourceModel("LemmaDeleteConfirmPanel.message",
                this, getDefaultModel()).setParameters("<b>" + lemma.getName() + "</b>");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConfirmationString() {
        return getString("LemmaDeleteConfirmPanel.confirm");
    }

    /**
     * Does nothing.
     */
    @Override
    public void onCancel() {
    }

    /**
     * Removes the lemma of the default model.
     *
     * @param target target that produces an Ajax response
     */
    @Override
    public void onConfirm(AjaxRequestTarget target) {
        new LemmaDao().remove((Lemma) getDefaultModelObject());
    }
}
