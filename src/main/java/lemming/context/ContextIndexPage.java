package lemming.context;

import lemming.auth.WebSession;
import lemming.data.GenericDataProvider;
import lemming.table.AutoShrinkBehavior;
import lemming.table.FilterUpdatingBehavior;
import lemming.table.GenericDataTable;
import lemming.table.TextFilterColumn;
import lemming.ui.TitleLabel;
import lemming.ui.page.IndexBasePage;
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
@AuthorizeInstantiation({"SIGNED_IN"})
public class ContextIndexPage extends IndexBasePage {
    /**
     * True if the filter form shall be enabled.
     */
    private static final Boolean FILTER_FORM_ENABLED = false;

    /**
     * Creates a context index page.
     */
    public ContextIndexPage() {
        GenericDataProvider<Context> dataProvider = new GenericDataProvider<>(Context.class,
                new SortParam<>("keyword", true));
        FilterForm<Context> filterForm = new FilterForm<>("filterForm", dataProvider);
        TextField<String> filterTextField = new TextField<>("filterTextField", Model.of(""));
        WebMarkupContainer container = new WebMarkupContainer("container");
        Fragment fragment;
        GenericDataTable<Context> dataTable;

        // check if the session is expired
        WebSession.get().checkSessionExpired();

        if (FILTER_FORM_ENABLED) {
            fragment = new Fragment("fragment", "withFilterForm", this);
            dataTable = new GenericDataTable<>("contextDataTable", getColumns(), dataProvider, filterForm);

            filterTextField.add(new FilterUpdatingBehavior(filterTextField, dataTable, dataProvider));
            filterForm.add(dataTable);
            fragment.add(filterForm);
        } else {
            fragment = new Fragment("fragment", "withoutFilterForm", this);
            dataTable = new GenericDataTable<>("contextDataTable", getColumns(), dataProvider);

            filterTextField.add(new FilterUpdatingBehavior(filterTextField, dataTable, dataProvider));
            fragment.add(dataTable);
        }

        add(new ContextDeleteConfirmPanel(dataTable));
        add(new FeedbackPanel());
        add(filterTextField);
        add(new NewButton());
        add(new BatchProcessingButton());
        add(container);
        container.add(fragment);
        // auto-shrink following and preceding text columns
        add(new AutoShrinkBehavior());
    }

    /**
     * Called when a context index page is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new TitleLabel(getString("ContextIndexPage.header")));
    }

    /**
     * Returns the list of columns of the data table.
     *
     * @return A list of columns.
     */
    private List<IColumn<Context, String>> getColumns() {
        List<IColumn<Context, String>> columns = new ArrayList<>();

        columns.add(new NumberTextFilterColumn(Model.of(getString("Context.number")),
                "number", "number"));
        columns.add(new TextFilterColumn<>(Model.of(getString("Context.lemma")),
                "lemmaString", "lemmaString"));
        columns.add(new TextFilterColumn<>(Model.of(getString("Context.pos")),
                "posString", "posString"));
        columns.add(new TextFilterColumn<Context, Context, String>(Model.of(getString("Context.location")),
                "location", "location"));
        columns.add(new PrecedingContextTextFilterColumn(Model.of(getString("Context.preceding")),
                "preceding", "preceding"));
        columns.add(new KeywordTextFilterColumn(Model.of(getString("Context.keyword")),
                "keyword", "keyword"));
        columns.add(new FollowingContextTextFilterColumn(Model.of(getString("Context.following")),
                "following", "following"));
        columns.add(new ContextActionPanelColumn(Model.of("")));

        return columns;
    }

    /**
     * A button which runs a context edit form to create a new context.
     */
    private class NewButton extends Link<Void> {
        /**
         * Creates a button.
         */
        public NewButton() {
            super("new");
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
         * Creates a batch processing button.
         */
        public BatchProcessingButton() {
            super("batchProcessing");
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
