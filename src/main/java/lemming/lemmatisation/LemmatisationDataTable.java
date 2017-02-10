package lemming.lemmatisation;

import lemming.context.Context;
import lemming.data.GenericDataProvider;
import lemming.table.NavigationToolbar;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.*;

/**
 * A custom data table with toolbars and data provider for context lemmatisation.
 */
public class LemmatisationDataTable extends DataTable<Context, String> {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default rows per page.
     */
    private static final long DEFAULT_ROWS_PER_PAGE = 100;

    /**
     * A map of row indexes and row models.
     */
    private Map<Integer, IModel<Context>> rowModels;

    /**
     * IDs of selected contexts.
     */
    private CollectionModel<Integer> selectedContextIds;

    /**
     * Creates a new data table with toolbars.
     *
     * @param id
     *            ID of a data table
     * @param columns
     *            list of columns
     * @param dataProvider
     *            provides data for a table
     */
    public LemmatisationDataTable(String id, List<IColumn<Context, String>> columns,
                                  GenericDataProvider<Context> dataProvider) {
        super(id, columns, dataProvider, DEFAULT_ROWS_PER_PAGE);
        createTable(dataProvider, null);
    }

    /**
     * Creates a new data table with toolbars.
     *
     * @param id
     *            ID of a data table
     * @param columns
     *            list of columns
     * @param dataProvider
     *            provides data for a table
     * @param filterForm
     *            form that filters data of a table
     */
    public LemmatisationDataTable(String id, List<IColumn<Context, String>> columns,
                                  GenericDataProvider<Context> dataProvider, FilterForm<Context> filterForm) {
        super(id, columns, dataProvider, DEFAULT_ROWS_PER_PAGE);
        createTable(dataProvider, filterForm);
    }

    /**
     * Builds a new data table with toolbars.
     *
     * @param dataProvider
     *            provides data for a table
     * @param filterForm
     *            form that filters data of a table
     */
    private void createTable(GenericDataProvider<Context> dataProvider, FilterForm<Context> filterForm) {
        setOutputMarkupId(true);
        add(AttributeModifier.append("class", "table table-hover table-striped selectable"));
        addTopToolbar(new NavigationToolbar<Context>(this));
        addTopToolbar(new HeadersToolbar<String>(this, dataProvider));
        addBottomToolbar(new NoRecordsToolbar(this));

        if (filterForm instanceof FilterForm) {
            addTopToolbar(new FilterToolbar(this, filterForm));
        }

        rowModels = new HashMap<Integer, IModel<Context>>(new Long(DEFAULT_ROWS_PER_PAGE).intValue());
        selectedContextIds = new CollectionModel<Integer>(new ArrayList<Integer>());
    }

    /**
     * Creates a new row item.
     *
     * @param id ID of a row item
     * @param index index of a row item
     * @param model model of a row item
     * @return A row item.
     */
    @Override
    protected Item<Context> newRowItem(String id, int index, IModel<Context> model) {
        Item<Context> rowItem = super.newRowItem(id, index, model);
        rowItem.add(new RowSelectBehavior());

        if (selectedContextIds.getObject().contains(model.getObject().getId())) {
            model.getObject().setSelected(true);
            rowItem.add(AttributeModifier.append("class", "selected"));
        }

        rowModels.put(index, model);
        return rowItem;
    }

    /**
     * Returns the row models of the table.
     *
     * @return A collection of context models.
     */
    public Collection<IModel<Context>> getRowModels() {
        return rowModels.values();
    }

    /**
     * Updates the IDs of selected contexts.
     *
     * @param selectedContextIds a collection of context IDs
     */
    public void updateSelectedContexts(CollectionModel<Integer> selectedContextIds) {
        this.selectedContextIds = selectedContextIds;
    }

    /**
     * A behavior enabling ctrl/shift row selection.
     */
    private class RowSelectBehavior extends Behavior {
        /**
         * Renders to the web response what the component wants to contribute.
         *
         * @param component component object
         * @param response response object
         */
        @Override
        public void renderHead(Component component, IHeaderResponse response) {
            PackageResourceReference javaScriptReference = new JavaScriptResourceReference(LemmatisationDataTable.class,
                    "scripts/row-select.js");
            response.render(JavaScriptHeaderItem.forReference(javaScriptReference));
        }
    }
}
