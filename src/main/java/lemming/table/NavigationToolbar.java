package lemming.table;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxNavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * An extended AjaxNavigationToolbar with a custom paging navigator.
 *
 * @param <T>
 *            data type that is navigated
 * @see PagingNavigator
 */
public class NavigationToolbar<T> extends AjaxNavigationToolbar {
    /**
     * A navigation form with a go to page field.
     */
    private Panel navigatorFormPanel;

    /**
     * Creates a navigation toolbar.
     *
     * @param table
     *            table a toolbar is attached to
     */
    public NavigationToolbar(DataTable<T, String> table) {
        super(table);
        navigatorFormPanel = new PagingNavigatorFormPanel<>("navigatorFormPanel", table);
    }

    /**
     * Called when a AjaxNavigationToolbar is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();

        for (Component component : this) {
            if (component.getId().equals("span")) {
                WebMarkupContainer spanContainer = (WebMarkupContainer) component;
                spanContainer.add(navigatorFormPanel);
            }
        }
    }

    /**
     * Creates a paging navigator.
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
