package lemming.lemmatization;

import lemming.context.Context;
import lemming.context.ContextDao;
import lemming.lemma.Lemma;
import lemming.lemma.LemmaAutoCompleteTextField;
import lemming.lemma.LemmaDao;
import lemming.ui.panel.ModalFormPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
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
    private LemmatizationDataTable dataTable;

    /**
     * A auto-complete textfield for lemmata.
     */
    private LemmaAutoCompleteTextField lemmaTextField;

    /**
     * Creates a set lemma panel.
     *
     * @param dataTable a data table which delivers row models
     */
    public SetLemmaPanel(LemmatizationDataTable dataTable) {
        super("setLemmaPanel");
        this.dataTable = dataTable;
        lemmaTextField = new LemmaAutoCompleteTextField("lemma", new Model<>());
        addFormComponent(lemmaTextField);
    }

    /**
     * Renders to the web response what the component wants to contribute.
     *
     * @param response response object
     */
    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        // ctrl + l
        String javaScript = "jQuery(window).on('keydown', function (event) { " +
                "var modifier = event.ctrlKey || event.metaKey; " +
                "if (modifier && event.which === 76) { " +
                "jQuery('#" + getModalWindowId() + "').modal('show'); " +
                "event.preventDefault(); event.stopPropagation(); } });";
        response.render(OnDomReadyHeaderItem.forScript(javaScript));
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
        CollectionModel<Integer> selectedContextIds = new CollectionModel<>(new ArrayList<>());
        ContextDao contextDao = new ContextDao();

        if (lemma != null) {
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
