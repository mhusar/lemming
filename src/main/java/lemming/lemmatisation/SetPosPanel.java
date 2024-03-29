package lemming.lemmatisation;

import lemming.context.Context;
import lemming.context.ContextDao;
import lemming.pos.Pos;
import lemming.pos.PosAutoCompleteTextField;
import lemming.pos.PosDao;
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
 * A modal dialog to set a part of speech for row models of a data table.
 */
class SetPosPanel extends ModalFormPanel {
    /**
     * A data table.
     */
    private final LemmatisationDataTable dataTable;

    /**
     * A auto-complete textfield for parts of speech.
     */
    private final PosAutoCompleteTextField posTextField;

    /**
     * Creates a set part of speech panel.
     *
     * @param dataTable a data table which delivers row models
     */
    public SetPosPanel(LemmatisationDataTable dataTable) {
        super("setPosPanel");
        this.dataTable = dataTable;
        posTextField = new PosAutoCompleteTextField(new Model<>());
        addFormComponent(posTextField);
        enableClearButton();
    }

    /**
     * Renders to the web response what the component wants to contribute.
     *
     * @param response response object
     */
    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        // ctrl + p
        String javaScript = "jQuery(window).on('keydown', function (event) { " +
                "var modifier = event.ctrlKey || event.metaKey; " +
                "if (modifier && event.which === 80) { " +
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
        return getString("SetPosPanel.setPos");
    }

    /**
     * Called when the modal dialog is used to clear data.
     *
     * @param target target that produces an Ajax response
     * @param form   form that is submitted
     */
    @Override
    public void onClear(AjaxRequestTarget target, Form<?> form) {
        Collection<IModel<Context>> rowModels = dataTable.getRowModels();
        CollectionModel<Integer> selectedContextIds = new CollectionModel<>(new ArrayList<>());
        ContextDao contextDao = new ContextDao();

        for (IModel<Context> rowModel : rowModels) {
            if (rowModel.getObject().getSelected()) {
                Context context = rowModel.getObject();
                context.setPos(null);
                context.setPosString(null);
                contextDao.merge(context);
                selectedContextIds.getObject().add(context.getId());
            }
        }

        dataTable.updateSelectedContexts(selectedContextIds);
        target.add(dataTable);
    }

    /**
     * Confirms the dialog when clicked.
     *
     * @param target target that produces an Ajax response
     * @param form   form that is submitted
     */
    @Override
    public void onConfirm(AjaxRequestTarget target, Form<?> form) {
        String posName = posTextField.getInput();
        Pos pos = new PosDao().findByName(posName);
        Collection<IModel<Context>> rowModels = dataTable.getRowModels();
        CollectionModel<Integer> selectedContextIds = new CollectionModel<>(new ArrayList<>());
        ContextDao contextDao = new ContextDao();

        if (pos != null) {
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
