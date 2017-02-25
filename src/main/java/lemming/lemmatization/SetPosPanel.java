package lemming.lemmatization;

import lemming.context.Context;
import lemming.context.ContextDao;
import lemming.pos.Pos;
import lemming.pos.PosAutoCompleteTextField;
import lemming.pos.PosDao;
import lemming.ui.panel.ModalFormPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.CollectionModel;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A modal dialog to set a part of speech for row models of a data table.
 */
class SetPosPanel extends ModalFormPanel {
    /**
     * A data table.
     */
    private LemmatizationDataTable dataTable;

    /**
     * A auto-complete textfield for parts of speech.
     */
    private PosAutoCompleteTextField posTextField;

    /**
     * Creates a set part of speech panel.
     *
     * @param id ID of the panel
     * @param parentForm a parent form
     * @param dataTable a data table which delivers row models
     */
    public SetPosPanel(String id, Form<Context> parentForm, LemmatizationDataTable dataTable) {
        super(id, parentForm);
        this.dataTable = dataTable;
        posTextField = new PosAutoCompleteTextField("pos", new Model<Pos>());
        addFormComponent(posTextField);
    }

    /**
     * Returns the title string.
     *
     * @return A localized string.
     */
    @Override
    public String getTitleString() {
        return getString("SetPosPanel.setPos");
    }

    /**
     * Confirms the dialog when clicked.
     *
     * @param target target that produces an Ajax response
     * @param form form that is submitted
     */
    @Override
    public void onConfirm(AjaxRequestTarget target, Form<?> form) {
        String posName = posTextField.getInput();
        Pos pos = new PosDao().findByName(posName);
        Collection<IModel<Context>> rowModels = dataTable.getRowModels();
        CollectionModel<Integer> selectedContextIds = new CollectionModel<Integer>(new ArrayList<Integer>());
        ContextDao contextDao = new ContextDao();

        if (pos instanceof Pos) {
            for (IModel<Context> rowModel : rowModels) {
                if (rowModel.getObject().getSelected()) {
                    Context context = rowModel.getObject();
                    context.setPos(pos);
                    contextDao.merge(context);
                    selectedContextIds.getObject().add(context.getId());
                }
            }

            dataTable.updateSelectedContexts(selectedContextIds);
            target.add(dataTable);
        }
    }
}
