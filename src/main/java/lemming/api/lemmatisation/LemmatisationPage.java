package lemming.api.lemmatisation;

import lemming.api.auth.WebSession;
import lemming.api.context.*;
import lemming.api.data.GenericDataProvider;
import lemming.api.lemma.LemmaTextFilterColumn;
import lemming.api.pos.PosTextFilterColumn;
import lemming.api.table.TextFilterColumn;
import lemming.api.ui.page.BasePage;
import lemming.api.ui.panel.FeedbackPanel;
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
 * An index page that lists all available contexts in a data table for lemmatization.
 */
@AuthorizeInstantiation({ "SIGNED_IN" })
public class LemmatisationPage extends BasePage {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * True if the filter form shall be enabled.
     */
    private static final Boolean FILTER_FORM_ENABLED = false;

    /**
     * Creates a lemmatisation page.
     */
    public LemmatisationPage() {
        GenericDataProvider<Context> dataProvider = new GenericDataProvider<Context>(Context.class,
                new SortParam<String>("lemma.name", true));
        FilterForm<Context> filterForm = new FilterForm<Context>("filterForm", dataProvider);
        TextField<String> filterTextField = new TextField<String>("filterTextField", Model.of(""));
        WebMarkupContainer container = new WebMarkupContainer("container");
        Fragment fragment;
        LemmatisationDataTable dataTable;

        // check if the session is expired
        WebSession.get().checkSessionExpired(getPageClass());

        if (FILTER_FORM_ENABLED) {
            fragment = new Fragment("fragment", "withFilterForm", this);
            dataTable = new LemmatisationDataTable("lemmatisationDataTable", getColumns(), dataProvider, filterForm);

            filterTextField.add(new FilterUpdatingBehavior(filterTextField, dataTable, dataProvider));
            filterForm.add(dataTable);
            fragment.add(filterForm);
        } else {
            fragment = new Fragment("fragment", "withoutFilterForm", this);
            dataTable = new LemmatisationDataTable("lemmatisationDataTable", getColumns(), dataProvider);

            filterTextField.add(new FilterUpdatingBehavior(filterTextField, dataTable, dataProvider));
            fragment.add(dataTable);
        }

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
    private List<IColumn<Context, String>> getColumns() {
        List<IColumn<Context, String>> columns = new ArrayList<>();

        columns.add(new LemmaTextFilterColumn<>(Model.of(getString("Context.lemma")),
                "lemma.name", "lemma"));
        columns.add(new PosTextFilterColumn<>(Model.of(getString("Context.pos")),
                "pos.name", "pos"));
        columns.add(new TextFilterColumn<>(Model.of(getString("Context.location")),
                "location", "location"));
        columns.add(new PrecedingContextTextFilterColumn(Model.of(getString("Context.preceding")), "preceding",
                "preceding", 35));
        columns.add(new KeywordTextFilterColumn(Model.of(getString("Context.keyword")), "keyword",
                "keyword"));
        columns.add(new FollowingContextTextFilterColumn(Model.of(getString("Context.following")), "following",
                "following", 35));

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
        LemmatisationDataTable dataTable;

        /**
         * Data provider which delivers data for the table.
         */
        GenericDataProvider<Context> dataProvider;

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
        public FilterUpdatingBehavior(TextField<String> textField, LemmatisationDataTable dataTable,
                                      GenericDataProvider<Context> dataProvider) {
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

