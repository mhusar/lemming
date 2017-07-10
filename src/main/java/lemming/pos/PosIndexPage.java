package lemming.pos;

import lemming.auth.WebSession;
import lemming.data.GenericDataProvider;
import lemming.data.SourceTextFilterColumn;
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
 * An index page that lists all available parts of speech in a data table.
 */
@AuthorizeInstantiation({ "SIGNED_IN" })
public class PosIndexPage extends IndexBasePage {
    /**
     * True if the filter form shall be enabled.
     */
    private static final Boolean FILTER_FORM_ENABLED = false;

    /**
     * Creates a pos index page.
     */
    public PosIndexPage() {
        GenericDataProvider<Pos> dataProvider = new GenericDataProvider<>(Pos.class,
                new SortParam<>("name", true));
        FilterForm<Pos> filterForm = new FilterForm<>("filterForm", dataProvider);
        TextField<String> filterTextField = new TextField<>("filterTextField", Model.of(""));
        WebMarkupContainer container = new WebMarkupContainer("container");
        Fragment fragment;
        GenericDataTable<Pos> dataTable;

        // check if the session is expired
        WebSession.get().checkSessionExpired();

        if (FILTER_FORM_ENABLED) {
            fragment = new Fragment("fragment", "withFilterForm", this);
            dataTable = new GenericDataTable<>("posDataTable", getColumns(), dataProvider, filterForm);

            filterTextField.add(new FilterUpdatingBehavior(filterTextField, dataTable, dataProvider));
            filterForm.add(dataTable);
            fragment.add(filterForm);
        } else {
            fragment = new Fragment("fragment", "withoutFilterForm", this);
            dataTable = new GenericDataTable<>("posDataTable", getColumns(), dataProvider);

            filterTextField.add(new FilterUpdatingBehavior(filterTextField, dataTable, dataProvider));
            fragment.add(dataTable);
        }

        add(new PosDeleteConfirmPanel(dataTable));
        add(new PosDeleteDeniedPanel(dataTable));
        add(new FeedbackPanel());
        add(filterTextField);
        add(new NewButton());
        add(new BatchProcessingButton());
        add(container);
        container.add(fragment);
    }

    /**
     * Called when a pos index page is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new TitleLabel(getString("PosIndexPage.header")));
    }

    /**
     * Returns the list of columns of the data table.
     * 
     * @return A list of columns.
     */
    private List<IColumn<Pos, String>> getColumns() {
        List<IColumn<Pos, String>> columns = new ArrayList<>();

        columns.add(new TextFilterColumn<Pos, Pos, String>(Model.of(getString("Pos.name")),
                "name", "name"));
        columns.add(new SourceTextFilterColumn<Pos, Pos, String>(Model.of(getString("Pos.source")),
                "source"));
        columns.add(new PosActionPanelColumn(Model.of("")));

        return columns;
    }

    /**
     * A button which runs a pos edit form to create a new pos.
     */
    private class NewButton extends Link<Void> {
        /**
         * Creates a button.
         *
         */
        public NewButton() {
            super("new");
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
         * Creates a batch processing button.
         *
         */
        public BatchProcessingButton() {
            super("batchProcessing");
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
