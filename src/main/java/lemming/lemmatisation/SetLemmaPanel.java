package lemming.lemmatisation;

import lemming.context.Context;
import lemming.context.ContextDao;
import lemming.lemma.Lemma;
import lemming.lemma.LemmaAutoCompleteTextField;
import lemming.lemma.LemmaDao;
import lemming.ui.panel.ModalFormPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.CollectionModel;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A modal dialog to set a lemma for row models of a data table.
 */
class SetLemmaPanel extends ModalFormPanel {
    /**
     * A data table.
     */
    private LemmatisationDataTable dataTable;

    /**
     * A auto-complete textfield for lemmata.
     */
    private LemmaAutoCompleteTextField lemmaTextField;

    /**
     * Creates a set lemma panel.
     *
     * @param id ID of the panel
     * @param parentForm a parent form
     * @param dataTable a data table which delivers row models
     */
    public SetLemmaPanel(String id, Form<Context> parentForm, LemmatisationDataTable dataTable) {
        super(id, parentForm);
        this.dataTable = dataTable;
        lemmaTextField = new LemmaAutoCompleteTextField("lemma", new Model<Lemma>());
        addFormComponent(lemmaTextField);
    }

    /**
     * Returns the title string.
     *
     * @return A localized string.
     */
    @Override
    public String getTitleString() {
        return getString("SetLemmaPanel.setLemma");
    }

    /**
     * Confirms the dialog when clicked.
     *
     * @param target target that produces an Ajax response
     * @param form form that is submitted
     */
    @Override
    public void onConfirm(AjaxRequestTarget target, Form<?> form) {
        String lemmaName = lemmaTextField.getInput();
        Lemma lemma = new LemmaDao().findByName(lemmaName);
        Collection<IModel<Context>> rowModels = dataTable.getRowModels();
        CollectionModel<Integer> selectedContextIds = new CollectionModel<Integer>(new ArrayList<Integer>());
        ContextDao contextDao = new ContextDao();

        if (lemma instanceof Lemma) {
            for (IModel<Context> rowModel : rowModels) {
                if (rowModel.getObject().getSelected()) {
                    Context context = rowModel.getObject();
                    context.setLemma(lemma);
                    contextDao.merge(context);
                    selectedContextIds.getObject().add(context.getId());
                }
            }

            dataTable.updateSelectedContexts(selectedContextIds);
            target.add(dataTable);
        }
    }
}