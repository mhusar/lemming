package lemming.table;

import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxNavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;

/**
 * An extended AjaxNavigationToolbar with a custom paging navigator.
 * 
 * @param <T>
 *            data type that is navigated
 * @see PagingNavigator
 */
public class NavigationToolbar<T> extends AjaxNavigationToolbar {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new navigation toolbar.
     * 
     * @param table
     *            table a toolbar is attached to
     */
    public NavigationToolbar(DataTable<T, String> table) {
        super(table);
    }

    /**
     * Creates a new paging navigator.
     * 
     * @param navigatorId
     * @param table
     *            table a paging navigator is attached to
     * @return A new paging navigator.
     */
    @Override
    protected PagingNavigator newPagingNavigator(String navigatorId, DataTable<?, ?> table) {
        return new PagingNavigator(navigatorId, table);
    }
}
