package lemming.table;

import java.lang.reflect.InvocationTargetException;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.core.util.lang.WicketObjects;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilteredAbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.GoAndClearFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import lemming.data.GenericDataProvider;

/**
 * A custom column that delivers go and clear filter as filter.
 *
 * @param <T> The type of element that is filtered.
 */
public class FilterPanelColumn<T> extends FilteredAbstractColumn<T, String> {
    /**
     * The class type that is filtered.
     */
    private final Class<T> typeClass;

    /**
     * Creates a filter panel column.
     *
     * @param displayModel title of a column
     * @param typeClass    class type that is filtered
     */
    protected FilterPanelColumn(IModel<String> displayModel, Class<T> typeClass) {
        super(displayModel);
        this.typeClass = typeClass;
    }

    /**
     * Returns a panel with a go and clear filter.
     *
     * @param componentId ID of the panel
     * @param form        form the panel is attached to
     * @return A panel with a go and clear filter.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Component getFilter(String componentId, FilterForm<?> form) {
        ResourceModel goModel = new ResourceModel(FilterPanelColumn.class.getSimpleName() + ".go");
        ResourceModel clearModel = new ResourceModel(FilterPanelColumn.class.getSimpleName() + ".clear");
        T originalState = null;

        try {
            originalState = (T) Class.forName(typeClass.getName()).getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return new GenericFilterPanel(componentId, Model.of(goModel.getObject()),
                Model.of(clearModel.getObject()), originalState);
    }

    /**
     * Populates a cell in a grid view. Must be overridden to be useful.
     *
     * @param cellItem    cell item that is populated
     * @param componentId ID of the child component
     * @param rowModel    model of the current row
     */
    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        cellItem.add(new Label(componentId));
    }

    /**
     * A generic go and clear panel.
     */
    private class GenericFilterPanel extends Panel {
        /**
         * Creates a go and clear panel.
         *
         * @param id            ID of the panel
         * @param goModel       label of the go button
         * @param clearModel    label of the clear button
         * @param originalState original state of the filter
         */
        public GenericFilterPanel(final String id, final IModel<String> goModel, final IModel<String> clearModel,
                                  final Object originalState) {
            super(id);
            add(new GenericGoAndClearFilter(goModel, clearModel, originalState));
        }
    }

    /**
     * A generic go and clear filter.
     */
    private class GenericGoAndClearFilter extends GoAndClearFilter {
        /**
         * Original state of the filter.
         */
        private final T originalState;

        /**
         * True if already configured.
         */
        private Boolean configured = false;

        /**
         * @param goModel       label of the go button
         * @param clearModel    label of the clear button
         * @param originalState the original state
         */
        @SuppressWarnings("unchecked")
        public GenericGoAndClearFilter(final IModel<String> goModel, final IModel<String> clearModel,
                                       Object originalState) {
            super("goAndClear", goModel, clearModel, originalState);
            this.originalState = (T) originalState;
        }

        /**
         * Called once per request before the filter is rendered.
         */
        @Override
        protected void onConfigure() {
            super.onConfigure();

            if (configured) {
                return;
            } else {
                configured = true;
            }

            getGoButton().add(AttributeModifier.append("class", "btn btn-default"));
            getClearButton().add(AttributeModifier.append("class", "btn btn-default"));
        }

        /**
         * Provides a behavior for the submit button.
         *
         * @param button the submit button
         */
        @Override
        @SuppressWarnings("unchecked")
        protected void onClearSubmit(final Button button) {
            FilterForm<T> form = (FilterForm<T>) button.getForm();
            GenericDataProvider<T> dataProvider = (GenericDataProvider<T>) form.getStateLocator();

            dataProvider.setFilterState(WicketObjects.cloneObject(originalState));
        }
    }
}
