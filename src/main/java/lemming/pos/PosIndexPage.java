package lemming.pos;

import lemming.auth.WebSession;
import lemming.data.GenericDataProvider;
import lemming.data.SourceTextFilterColumn;
import lemming.table.GenericDataTable;
import lemming.table.TextFilterColumn;
import lemming.ui.page.BasePage;
import lemming.ui.panel.FeedbackPanel;
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

import java.util.ArrayList;
import java.util.List;

/**
 * An index page that lists all available parts of speech in a data table.
 */
@AuthorizeInstantiation({ "SIGNED_IN" })
public class PosIndexPage extends BasePage {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * True if the filter form shall be enabled.
     */
    private static final Boolean FILTER_FORM_ENABLED = false;

    /**
     * Creates a pos index page.
     */
    public PosIndexPage() {
        GenericDataProvider<Pos> dataProvider = new GenericDataProvider<Pos>(Pos.class,
                new SortParam<String>("name", true));
        FilterForm<Pos> filterForm = new FilterForm<Pos>("filterForm", dataProvider);
        TextField<String> filterTextField = new TextField<String>("filterTextField", Model.of(""));
        WebMarkupContainer container = new WebMarkupContainer("container");
        Fragment fragment;
        GenericDataTable<Pos> dataTable;

        // check if the session is expired
        WebSession.get().checkSessionExpired();

        if (FILTER_FORM_ENABLED) {
            fragment = new Fragment("fragment", "withFilterForm", this);
            dataTable = new GenericDataTable<Pos>("posDataTable", getColumns(), dataProvider, filterForm);

            filterTextField.add(new FilterUpdatingBehavior(filterTextField, dataTable, dataProvider));
            filterForm.add(dataTable);
            fragment.add(filterForm);
        } else {
            fragment = new Fragment("fragment", "withoutFilterForm", this);
            dataTable = new GenericDataTable<Pos>("posDataTable", getColumns(), dataProvider);

            filterTextField.add(new FilterUpdatingBehavior(filterTextField, dataTable, dataProvider));
            fragment.add(dataTable);
        }

        add(new PosDeleteConfirmPanel("posDeleteConfirmPanel", dataTable));
        add(new PosDeleteDeniedPanel("posDeleteDeniedPanel", dataTable));
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
    private List<IColumn<Pos, String>> getColumns() {
        List<IColumn<Pos, String>> columns = new ArrayList<IColumn<Pos, String>>();

        columns.add(new TextFilterColumn<Pos, Pos, String>(Model.of(getString("Pos.name")),
                "name", "name"));
        columns.add(new SourceTextFilterColumn<Pos, Pos, String>(Model.of(getString("Pos.source")),
                "source", "source"));
        columns.add(new PosActionPanelColumn(Model.of("")));

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
        GenericDataTable<Pos> dataTable;

        /**
         * Data provider which delivers data for the table.
         */
        GenericDataProvider<Pos> dataProvider;

        /**
         * Creates a behavior.
         *
         * @param textField
         *            text field used a filter component
         * @param dataTable
         *            data table displaying filtered data
         * @param dataProvider
         *            data provider which delivers data for the table.
         */
        public FilterUpdatingBehavior(TextField<String> textField, GenericDataTable<Pos> dataTable,
                GenericDataProvider<Pos> dataProvider) {
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
     * A button which runs a pos edit form to create a new pos.
     */
    private class NewButton extends Link<Void> {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a button.
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
            setResponsePage(new PosEditPage(getPage().getPageClass()));
        }
    }

    /**
     * A button which runs a pos edit form to create new parts of speech.
     */
    private class BatchProcessingButton extends Link<Void> {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a batch processing button.
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
            setResponsePage(new PosEditPage());
        }
    }
}
