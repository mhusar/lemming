package lemming.lemma;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.model.StringResourceModel;

import lemming.table.GenericDataTable;
import lemming.ui.panel.ModalMessagePanel;

/**
 * A panel containing a modal window dialog stating that a lemma could not be
 * deleted.
 */
@AuthorizeAction(action = Action.RENDER, roles = { "SIGNED_IN" })
public class LemmaDeleteDeniedPanel extends ModalMessagePanel {
    /**
     * Creates a panel.
     *
     */
    public LemmaDeleteDeniedPanel() {
        super("lemmaDeleteDeniedPanel", DialogType.OKAY);
    }

    /**
     * Creates a panel.
     *
     * @param dataTable
     *            data table that is refreshed
     */
    public LemmaDeleteDeniedPanel(GenericDataTable<Lemma> dataTable) {
        super("lemmaDeleteDeniedPanel", DialogType.OKAY, dataTable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitleString() {
        return getString("LemmaDeleteDeniedPanel.title");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringResourceModel getMessageModel() {
        Lemma lemma = (Lemma) getDefaultModelObject();

        return new StringResourceModel("LemmaDeleteDeniedPanel.message",
                this, getDefaultModel()).setParameters("<b>" + lemma.getName() + "</b>");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConfirmationString() {
        return getString("LemmaDeleteDeniedPanel.confirm");
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
