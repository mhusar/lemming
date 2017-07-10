package lemming.sense;

import lemming.lemma.Lemma;
import lemming.table.GenericDataTable;
import lemming.ui.panel.ModalMessagePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import java.util.List;

/**
 * A panel containing a modal window dialog asking if a sense shall be deleted.
 */
@AuthorizeAction(action = Action.RENDER, roles = { "SIGNED_IN" })
public class SenseDeleteConfirmPanel extends ModalMessagePanel {
    /**
     * A tree of senses.
     */
    private SenseTree senseTree;

    /**
     * Model of the parent lemma.
     */
    private IModel<Lemma> lemmaModel;

    /**
     * Creates a panel.
     * 
     * @param id ID of the panel
     */
    public SenseDeleteConfirmPanel(String id) {
        super(id, DialogType.YES_NO);
    }

    /**
     * Creates a panel.
     *
     * @param dataTable data table that is refreshed
     */
    public SenseDeleteConfirmPanel(GenericDataTable<Sense> dataTable) {
        super("senseDeleteConfirmPanel", DialogType.YES_NO, dataTable);
    }

    /**
     * Creates a panel.
     *  @param lemmaModel model of the parent lemma
     * @param senseTree a tree of senses
     */
    public SenseDeleteConfirmPanel(IModel<Lemma> lemmaModel, SenseTree senseTree) {
        super("senseDeleteConfirmPanel", DialogType.YES_NO);
        this.lemmaModel = lemmaModel;
        this.senseTree = senseTree;
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
                this, getDefaultModel()).setParameters("<b>" + sense.getMeaning() + "</b>");
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
     *
     * @param target target that produces an Ajax response
     */
    @Override
    public void onConfirm(AjaxRequestTarget target) {
        Sense sense = (Sense) getDefaultModelObject();

        if (senseTree == null) {
            new SenseDao().remove(sense);
            return;
        }

        SenseDao senseDao = new SenseDao();
        Integer parentPosition = sense.getParentPosition();
        Integer childPosition = sense.getChildPosition();

        new SenseDao().remove(sense);
        List<Sense> rootNodes = senseDao.findRootNodes(lemmaModel.getObject());

        if (childPosition == null && parentPosition == 0) {
            if (!rootNodes.isEmpty()) {
                senseTree.select(target, rootNodes.get(parentPosition));
            } else {
                senseTree.deselect(target);
            }
        } else if (childPosition == null && parentPosition > 0) {
            senseTree.select(target, rootNodes.get(parentPosition - 1));
        } else if (childPosition == null || childPosition == 0) {
            senseTree.select(target, rootNodes.get(parentPosition));
        } else if (childPosition > 0) {
            Sense parentSense = senseDao.refresh(rootNodes.get(parentPosition));
            senseTree.select(target, parentSense.getChildren().get(childPosition - 1));
        }
    }
}
