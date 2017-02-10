package lemming.api.table;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * A TextFilteredColumn enabling row selection.
 *
 * @param <T> object type
 * @param <F> filter model type
 * @param <S> sort property type
 */
public class GenericRowSelectColumn<T,F,S> extends TextFilterColumn<T,F,S> {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new row selection column.
     *
     * @param displayModel title of a column
     * @param propertyExpression property expression of a column
     */
    public GenericRowSelectColumn(IModel<String> displayModel, String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    /**
     * Creates a new row selection column.
     *
     * @param displayModel model of a column
     * @param sortProperty sort property of a column
     * @param propertyExpression property expression of a column
     */
    public GenericRowSelectColumn(IModel<String> displayModel, S sortProperty, String propertyExpression) {
        super(displayModel, sortProperty, propertyExpression);
    }

    /**
     * Populates the current table cell item.
     *
     * @param item item representing the current table cell being rendered
     * @param componentId id of the component used to render the cell
     * @param rowModel model of the row item being rendered
     */
    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
        item.add(new CheckboxPanel(componentId, rowModel));
    }

    /**
     * A panel used to display a checkbox.
     */
    private class CheckboxPanel<T> extends Panel {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a new panel.
         *
         * @param id ID of the panel
         * @param model row model
         */
        public CheckboxPanel(String id, IModel<T> model) {
            super(id, model);
            add(new CheckBox("selected", new PropertyModel<Boolean>(model, getPropertyExpression())));
        }
    }
}
