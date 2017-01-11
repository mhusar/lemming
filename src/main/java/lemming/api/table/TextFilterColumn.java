package lemming.api.table;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

/**
 * A slightly manipulated TextFilteredPropertyColumn adding a CSS class to the
 * text field.
 * 
 * @param <T>
 *            object type
 * @param <F>
 *            filter model type
 * @param <S>
 *            sort property type
 */
public class TextFilterColumn<T, F, S> extends TextFilteredPropertyColumn<T, F, S> {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new TextFilterColumn.
     * 
     * @param displayModel
     *            title of a column
     * @param propertyExpression
     *            property expression of a column
     */
    public TextFilterColumn(IModel<String> displayModel, String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    /**
     * Creates a new TextFilterColumn.
     * 
     * @param displayModel
     *            title of a column
     * @param sortProperty
     *            sort property of a column
     * @param propertyExpression
     *            property expression of a column
     */
    public TextFilterColumn(IModel<String> displayModel, S sortProperty, String propertyExpression) {
        super(displayModel, sortProperty, propertyExpression);
    }

    /**
     * Returns the component used to filter a column.
     * 
     * @param componentId
     *            ID of the component
     * @param form
     *            filter form the component will be attached to
     * @return Component that will be used to represent a filter for the column.
     */
    @Override
    public Component getFilter(String componentId, FilterForm<?> form) {
        TextFilter<F> filter = new TextFilter<F>(componentId, getFilterModel(form), form);
        TextField<F> textField = filter.getFilter();

        textField.add(AttributeModifier.append("class", "form-control"));
        return filter;
    }
}
