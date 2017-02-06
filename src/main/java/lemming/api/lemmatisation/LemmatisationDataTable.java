package lemming.api.lemmatisation;

import lemming.api.context.Context;
import lemming.api.data.GenericDataProvider;
import lemming.api.table.NavigationToolbar;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

import java.util.List;

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
    protected Item<SelectableContextWrapper> newRowItem(String id, int index, IModel<SelectableContextWrapper> model) {
        Item<SelectableContextWrapper> rowItem = super.newRowItem(id, index, model);
        rowItem.setOutputMarkupId(true);
        return rowItem;
    }
}
