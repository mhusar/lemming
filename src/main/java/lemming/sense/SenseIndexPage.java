package lemming.sense;

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
        GenericDataProvider<Sense> dataProvider = new GenericDataProvider<Sense>(Sense.class,
                new SortParam<String>("lemmaString", true));
        FilterForm<Sense> filterForm = new FilterForm<Sense>("filterForm", dataProvider);
        TextField<String> filterTextField = new TextField<String>("filterTextField", Model.of(""));
        WebMarkupContainer container = new WebMarkupContainer("container");
        Fragment fragment;
        GenericDataTable<Sense> dataTable;

        // check if the session is expired
        WebSession.get().checkSessionExpired();

        if (FILTER_FORM_ENABLED) {
            fragment = new Fragment("fragment", "withFilterForm", this);
            dataTable = new GenericDataTable<Sense>("senseDataTable", getColumns(), dataProvider, filterForm);

            filterTextField.add(new FilterUpdatingBehavior(filterTextField, dataTable, dataProvider));
            filterForm.add(dataTable);
            fragment.add(filterForm);
        } else {
            fragment = new Fragment("fragment", "withoutFilterForm", this);
            dataTable = new GenericDataTable<Sense>("senseDataTable", getColumns(), dataProvider);

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
    private List<IColumn<Sense, String>> getColumns() {
        List<IColumn<Sense, String>> columns = new ArrayList<IColumn<Sense, String>>();

        columns.add(new TextFilterColumn<Sense, Sense, String>(Model.of(getString("Sense.lemma")),
                "lemmaString", "lemmaString"));
        columns.add(new TextFilterColumn<Sense, Sense, String>(Model.of(getString("Sense.meaning")),
                "meaning", "meaning"));
        columns.add(new SenseActionPanelColumn(Model.of("")));

        return columns;
    }
}
