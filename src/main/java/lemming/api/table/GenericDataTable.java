package lemming.api.table;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;

import lemming.api.data.GenericDataProvider;

/**
 * A custom data table with toolbars and data provider.
 * 
 * @param <T>
 *            class type that is rendered
 */
public class GenericDataTable<T> extends DataTable<T, String> {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Fixed rows per page.
     */
    private static final long ROWS_PER_PAGE = 12;

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
    public GenericDataTable(String id, List<IColumn<T, String>> columns, GenericDataProvider<T> dataProvider) {
        super(id, columns, dataProvider, ROWS_PER_PAGE);
        createTable(id, columns, dataProvider, null);
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
    public GenericDataTable(String id, List<IColumn<T, String>> columns, GenericDataProvider<T> dataProvider,
                            FilterForm<T> filterForm) {
        super(id, columns, dataProvider, ROWS_PER_PAGE);
        createTable(id, columns, dataProvider, filterForm);
    }

    /**
     * Builds a new data table with toolbars.
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
    private void createTable(String id, List<IColumn<T, String>> columns, GenericDataProvider<T> dataProvider,
                             FilterForm<T> filterForm) {
        setOutputMarkupId(true);
        add(AttributeModifier.append("class", "table table-hover table-striped"));
        addTopToolbar(new NavigationToolbar<T>(this));
        addTopToolbar(new HeadersToolbar<String>(this, dataProvider));
        addBottomToolbar(new NoRecordsToolbar(this));

        if (filterForm instanceof FilterForm) {
            addTopToolbar(new FilterToolbar(this, filterForm));
        }
    }
}
