package lemming.context;

import lemming.auth.WebSession;
import lemming.data.GenericDataProvider;
import lemming.table.FilterUpdatingBehavior;
import lemming.table.GenericDataTable;
import lemming.table.TextFilterColumn;
import lemming.ui.page.BasePage;
import lemming.ui.panel.FeedbackPanel;
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
 * An index page that lists all available contexts in a data table.
 */
@AuthorizeInstantiation({ "SIGNED_IN" })
public class ContextIndexPage extends BasePage {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * True if the filter form shall be enabled.
     */
    private static final Boolean FILTER_FORM_ENABLED = false;

    /**
     * Creates a context index page.
     */
    public ContextIndexPage() {
        GenericDataProvider<Context> dataProvider = new GenericDataProvider<Context>(Context.class,
                new SortParam<String>("keyword", true));
        FilterForm<Context> filterForm = new FilterForm<Context>("filterForm", dataProvider);
        TextField<String> filterTextField = new TextField<String>("filterTextField", Model.of(""));
        WebMarkupContainer container = new WebMarkupContainer("container");
        Fragment fragment;
        GenericDataTable<Context> dataTable;

        // check if the session is expired
        WebSession.get().checkSessionExpired();

        if (FILTER_FORM_ENABLED) {
            fragment = new Fragment("fragment", "withFilterForm", this);
            dataTable = new GenericDataTable<Context>("contextDataTable", getColumns(), dataProvider, filterForm);

            filterTextField.add(new FilterUpdatingBehavior(filterTextField, dataTable, dataProvider));
            filterForm.add(dataTable);
            fragment.add(filterForm);
        } else {
            fragment = new Fragment("fragment", "withoutFilterForm", this);
            dataTable = new GenericDataTable<Context>("contextDataTable", getColumns(), dataProvider);

            filterTextField.add(new FilterUpdatingBehavior(filterTextField, dataTable, dataProvider));
            fragment.add(dataTable);
        }

        add(new ContextDeleteConfirmPanel("contextDeleteConfirmPanel", dataTable));
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
    private List<IColumn<Context, String>> getColumns() {
        List<IColumn<Context, String>> columns = new ArrayList<IColumn<Context, String>>();

        columns.add(new TextFilterColumn<>(Model.of(getString("Context.lemma")),
                "lemmaString", "lemmaString"));
        columns.add(new TextFilterColumn<>(Model.of(getString("Context.pos")),
                "posString", "posString"));
        columns.add(new ContextTypeTextFilterColumn(Model.of(getString("Context.type")),
                "type", "type"));
        columns.add(new TextFilterColumn<Context, Context, String>(Model.of(getString("Context.location")),
                "location", "location"));
        columns.add(new PrecedingContextTextFilterColumn(Model.of(getString("Context.preceding")), "preceding",
                "preceding"));
        columns.add(new KeywordTextFilterColumn(Model.of(getString("Context.keyword")), "keyword",
                "keyword"));
        columns.add(new FollowingContextTextFilterColumn(Model.of(getString("Context.following")), "following",
                "following"));
        columns.add(new ContextActionPanelColumn(Model.of("")));

        return columns;
    }

    /**
     * A button which runs a context edit form to create a new context.
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
            setResponsePage(new ContextEditPage(getPage().getPageClass()));
        }
    }

    /**
     * A button which runs a context edit form to create new contexts.
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
            setResponsePage(new ContextEditPage());
        }
    }
}
