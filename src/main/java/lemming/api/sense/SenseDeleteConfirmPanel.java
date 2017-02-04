package lemming.api.sense;

import lemming.api.ui.panel.ModalMessagePanel;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.model.StringResourceModel;

/**
 * A panel containing a modal window dialog asking if a sense shall be deleted.
 */
@AuthorizeAction(action = Action.RENDER, roles = { "SIGNED_IN" })
public class SenseDeleteConfirmPanel extends ModalMessagePanel {
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
    public SenseDeleteConfirmPanel(String id, Page responsePage) {
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
    public SenseDeleteConfirmPanel(String id, Class<? extends Page> responsePageClass) {
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
    public SenseDeleteConfirmPanel(String id, SenseDataTable dataTable) {
        super(id, DialogType.YES_NO, dataTable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitleString() {
        return getString("SenseDeleteConfirmPanel.title");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringResourceModel getMessageModel() {
        Sense sense = (Sense) getDefaultModelObject();

        return new StringResourceModel("SenseDeleteConfirmPanel.message",
                (Component) this, getDefaultModel()).setParameters("<b>" + sense.getMeaning() + "</b>");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConfirmationString() {
        return getString("SenseDeleteConfirmPanel.confirm");
    }

    /**
     * Does nothing.
     */
    @Override
    public void onCancel() {
    }

    /**
     * Removes the sense of the default model.
     */
    @Override
    public void onConfirm() {
        new SenseDao().remove((Sense) getDefaultModelObject());
    }
}
