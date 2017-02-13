package lemming.sense;

import lemming.auth.WebSession;
import lemming.lemma.LemmaTextFilterColumn;
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
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * An index page that lists all available senses in a data table.
 */
@AuthorizeInstantiation({ "SIGNED_IN" })
public class SenseIndexPage extends BasePage {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * True if the filter form shall be enabled.
     */
    private static final Boolean FILTER_FORM_ENABLED = false;

    /**
     * Creates a sense index page.
     */
    public SenseIndexPage() {
        SenseDataProvider dataProvider = new SenseDataProvider(SenseWrapper.class,
                new SortParam<String>("name", true));
        FilterForm<SenseWrapper> filterForm = new FilterForm<SenseWrapper>("filterForm", dataProvider);
        TextField<String> filterTextField = new TextField<String>("filterTextField", Model.of(""));
        WebMarkupContainer container = new WebMarkupContainer("container");
        Fragment fragment;
        SenseDataTable dataTable;

        // check if the session is expired
        WebSession.get().checkSessionExpired(getPageClass());

        if (FILTER_FORM_ENABLED) {
            fragment = new Fragment("fragment", "withFilterForm", this);
            dataTable = new SenseDataTable("senseDataTable", getColumns(), dataProvider, filterForm);

            filterTextField.add(new FilterUpdatingBehavior(filterTextField, dataTable, dataProvider));
            filterForm.add(dataTable);
            fragment.add(filterForm);
        } else {
            fragment = new Fragment("fragment", "withoutFilterForm", this);
            dataTable = new SenseDataTable("senseDataTable", getColumns(), dataProvider);

            filterTextField.add(new FilterUpdatingBehavior(filterTextField, dataTable, dataProvider));
            fragment.add(dataTable);
        }

        add(new SenseDeleteConfirmPanel("senseDeleteConfirmPanel", dataTable));
        add(new FeedbackPanel("feedbackPanel"));
        add(filterTextField);
        add(container);
        container.add(fragment);
    }

    /**
     * Returns the list of columns of the data table.
     * 
     * @return A list of columns.
     */
    private List<IColumn<SenseWrapper, String>> getColumns() {
        List<IColumn<SenseWrapper, String>> columns = new ArrayList<IColumn<SenseWrapper, String>>();

        columns.add(new LemmaTextFilterColumn<SenseWrapper, SenseWrapper, String>(Model.of(getString("Sense.lemma")),
                "lemma", "lemma"));
        columns.add(new TextFilterColumn<SenseWrapper, SenseWrapper, String>(Model.of(getString("Sense.meaning")),
                "sense", "sense.meaning"));
        columns.add(new SenseActionPanelColumn(Model.of("")));

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
        SenseDataTable dataTable;

        /**
         * Data provider which delivers data for the table.
         */
        SenseDataProvider dataProvider;

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
        public FilterUpdatingBehavior(TextField<String> textField, SenseDataTable dataTable,
                SenseDataProvider dataProvider) {
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
}
