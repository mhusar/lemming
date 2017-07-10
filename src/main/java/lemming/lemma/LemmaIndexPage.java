package lemming.lemma;

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
 * An index page that lists all available lemmata in a data table.
 */
@AuthorizeInstantiation({ "SIGNED_IN" })
public class LemmaIndexPage extends IndexBasePage {
    /**
     * True if the filter form shall be enabled.
     */
    private static final Boolean FILTER_FORM_ENABLED = false;

    /**
     * Creates a lemma index page.
     */
    public LemmaIndexPage() {
        GenericDataProvider<Lemma> dataProvider = new GenericDataProvider<>(Lemma.class,
                new SortParam<>("name", true));
        FilterForm<Lemma> filterForm = new FilterForm<>("filterForm", dataProvider);
        TextField<String> filterTextField = new TextField<>("filterTextField", Model.of(""));
        WebMarkupContainer container = new WebMarkupContainer("container");
        Fragment fragment;
        GenericDataTable<Lemma> dataTable;

        // check if the session is expired
        WebSession.get().checkSessionExpired();

        if (FILTER_FORM_ENABLED) {
            fragment = new Fragment("fragment", "withFilterForm", this);
            dataTable = new GenericDataTable<>("lemmaDataTable", getColumns(), dataProvider, filterForm);

            filterTextField.add(new FilterUpdatingBehavior(filterTextField, dataTable, dataProvider));
            filterForm.add(dataTable);
            fragment.add(filterForm);
        } else {
            fragment = new Fragment("fragment", "withoutFilterForm", this);
            dataTable = new GenericDataTable<>("lemmaDataTable", getColumns(), dataProvider);

            filterTextField.add(new FilterUpdatingBehavior(filterTextField, dataTable, dataProvider));
            fragment.add(dataTable);
        }

        add(new LemmaDeleteConfirmPanel(dataTable));
        add(new LemmaDeleteDeniedPanel(dataTable));
        add(new FeedbackPanel());
        add(filterTextField);
        add(new NewButton());
        add(new BatchProcessingButton());
        add(container);
        container.add(fragment);
    }

    /**
     * Called when a lemma index page is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new TitleLabel(getString("LemmaIndexPage.header")));
    }

    /**
     * Returns the list of columns of the data table.
     * 
     * @return A list of columns.
     */
    private List<IColumn<Lemma, String>> getColumns() {
        List<IColumn<Lemma, String>> columns = new ArrayList<>();

        columns.add(new TextFilterColumn<Lemma, Lemma, String>(Model.of(getString("Lemma.name")),
                "name", "name"));
        columns.add(new TextFilterColumn<Lemma, Lemma, String>(Model.of(getString("Lemma.replacement")),
                "replacementString", "replacementString"));
        columns.add(new TextFilterColumn<Lemma, Lemma, String>(Model.of(getString("Lemma.pos")),
                "posString", "posString"));
        columns.add(new SourceTextFilterColumn<Lemma, Lemma, String>(Model.of(getString("Lemma.source")),
                "source"));
        columns.add(new TextFilterColumn<Lemma, Lemma, String>(Model.of(getString("Lemma.reference")),
                "reference", "reference"));
        columns.add(new LemmaActionPanelColumn(Model.of("")));

        return columns;
    }

    /**
     * A button which runs a lemma edit form to create a new lemma.
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
            setResponsePage(new LemmaEditPage(getPage().getPageClass()));
        }
    }

    /**
     * A button which runs a lemma edit form to create new lemmata.
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
            setResponsePage(new LemmaEditPage());
        }
    }
}
