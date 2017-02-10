package lemming.lemma;

import java.util.ArrayList;
import java.util.List;

import lemming.data.SourceTextFilterColumn;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.Model;

import lemming.auth.WebSession;
import lemming.data.GenericDataProvider;
import lemming.table.GenericDataTable;
import lemming.table.TextFilterColumn;
import lemming.ui.page.BasePage;
import lemming.ui.panel.FeedbackPanel;

/**
 * An index page that lists all available lemmata in a data table.
 */
@AuthorizeInstantiation({ "SIGNED_IN" })
public class LemmaIndexPage extends BasePage {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * True if the filter form shall be enabled.
     */
    private static final Boolean FILTER_FORM_ENABLED = false;

    /**
     * Creates a new lemma index page.
     */
    public LemmaIndexPage() {
        GenericDataProvider<Lemma> dataProvider = new GenericDataProvider<Lemma>(Lemma.class,
                new SortParam<String>("name", true));
        FilterForm<Lemma> filterForm = new FilterForm<Lemma>("filterForm", dataProvider);
        TextField<String> filterTextField = new TextField<String>("filterTextField", Model.of(""));
        WebMarkupContainer container = new WebMarkupContainer("container");
        Fragment fragment;
        GenericDataTable<Lemma> dataTable;

        // check if the session is expired
        WebSession.get().checkSessionExpired(getPageClass());

        if (FILTER_FORM_ENABLED) {
            fragment = new Fragment("fragment", "withFilterForm", this);
            dataTable = new GenericDataTable<Lemma>("lemmaDataTable", getColumns(), dataProvider, filterForm);

            filterTextField.add(new FilterUpdatingBehavior(filterTextField, dataTable, dataProvider));
            filterForm.add(dataTable);
            fragment.add(filterForm);
        } else {
            fragment = new Fragment("fragment", "withoutFilterForm", this);
            dataTable = new GenericDataTable<Lemma>("lemmaDataTable", getColumns(), dataProvider);

            filterTextField.add(new FilterUpdatingBehavior(filterTextField, dataTable, dataProvider));
            fragment.add(dataTable);
        }

        add(new LemmaDeleteConfirmPanel("lemmaDeleteConfirmPanel", dataTable));
        add(new LemmaDeleteDeniedPanel("lemmaDeleteDeniedPanel", dataTable));
        add(new FeedbackPanel("feedbackPanel"));
        add(filterTextField);
        add(new NewButton("new"));
        add(new BatchProcessingButton("batchProcessing"));
        add(container);
        container.add(fragment);
    }

    /**
     * Returns the list of columns of the data table.
     * 
     * @return A list of columns.
     */
    private List<IColumn<Lemma, String>> getColumns() {
        List<IColumn<Lemma, String>> columns = new ArrayList<IColumn<Lemma, String>>();

        columns.add(new TextFilterColumn<Lemma, Lemma, String>(Model.of(getString("Lemma.name")),
                "name", "name"));
        columns.add(new TextFilterColumn<Lemma, Lemma, String>(Model.of(getString("Lemma.replacement")),
                "replacementString", "replacementString"));
        columns.add(new TextFilterColumn<Lemma, Lemma, String>(Model.of(getString("Lemma.pos")),
                "posString", "posString"));
        columns.add(new SourceTextFilterColumn<Lemma, Lemma, String>(Model.of(getString("Lemma.source")),
                "source", "source"));
        columns.add(new TextFilterColumn<Lemma, Lemma, String>(Model.of(getString("Lemma.reference")),
                "reference", "reference"));
        columns.add(new LemmaActionPanelColumn(Model.of("")));

        return columns;
    }

    /**
     * Implementation of a form component updating behavior for a filter text field.
     */
    private class FilterUpdatingBehavior extends AjaxFormComponentUpdatingBehavior {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * The text field used as filter component.
         */
        TextField<String> textField;

        /**
         * Data table displaying filtered data.
         */
        GenericDataTable<Lemma> dataTable;

        /**
         * Data provider which delivers data for the table.
         */
        GenericDataProvider<Lemma> dataProvider;

        /**
         * Creates a new behavior.
         * 
         * @param textField
         *            text field used a filter component
         * @param dataTable
         *            data table displaying filtered data
         * @param dataProvider
         *            data provider which delivers data for the table.
         */
        public FilterUpdatingBehavior(TextField<String> textField, GenericDataTable<Lemma> dataTable,
                GenericDataProvider<Lemma> dataProvider) {
            super("input");
            this.textField = textField;
            this.dataTable = dataTable;
            this.dataProvider = dataProvider;
        }

        /**
         * Called when the text field content changes.
         * 
         * @param target
         *            target that produces an Ajax response
         */
        @Override
        protected void onUpdate(AjaxRequestTarget target) {
            dataProvider.updateFilter(textField.getInput());
            target.add(dataTable);
        }
    }

    /**
     * A button which runs a lemma edit form to create a new lemma.
     */
    private class NewButton extends Link<Void> {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a new button.
         * 
         * @param id
         *            ID of the button
         */
        public NewButton(String id) {
            super(id);
        }

        /**
         * Called on button click.
         */
        @Override
        public void onClick() {
            setResponsePage(new LemmaEditPage(getPage().getPageClass()));
        }
    }

    /**
     * A button which runs a lemma edit form to create new lemmata.
     */
    private class BatchProcessingButton extends Link<Void> {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a new batch processing button.
         * 
         * @param id
         *            ID of the button
         */
        public BatchProcessingButton(String id) {
            super(id);
        }

        /**
         * Called on button click.
         */
        @Override
        public void onClick() {
            setResponsePage(new LemmaEditPage());
        }
    }
}
