package lemming.context;

import lemming.table.TextFilterColumn;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

/**
 * A TextFilteredColumn to display first numbers properly.
 * <p>
 * This column adds class "first-child" to be able to style this column as first child of a row.
 */
public class NumberTextFilterColumn extends TextFilterColumn<Context, Context, String> {

    /**
     * Creates a TextFilterColumn for contexts.
     *
     * @param displayModel       title of a column
     * @param propertyExpression property expression of a column
     */
    public NumberTextFilterColumn(IModel<String> displayModel, String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    /**
     * Creates a TextFilterColumn for contexts.
     *
     * @param displayModel       title of a column
     * @param sortProperty       sort property of a column
     * @param propertyExpression property expression of a column
     */
    public NumberTextFilterColumn(IModel<String> displayModel, String sortProperty, String propertyExpression) {
        super(displayModel, sortProperty, propertyExpression);
    }

    /**
     * Populates the current table cell item.
     *
     * @param item        item representing the current table cell being rendered
     * @param componentId id of the component used to render the cell
     * @param rowModel    model of the row item being rendered
     */
    @Override
    public void populateItem(Item<ICellPopulator<Context>> item, String componentId, IModel<Context> rowModel) {
        Context context = rowModel.getObject();
        item.add(new ContextPanel(componentId, String.valueOf(context.getNumber())))
                .add(AttributeModifier.append("class", "first-child number"));
    }

    /**
     * Returns the header component of a column.
     *
     * @param componentId ID of the component
     * @return A header component.
     */
    @Override
    public Component getHeader(String componentId) {
        Component header = super.getHeader(componentId);
        header.add(AttributeModifier.append("class", "first-child number"));
        return header;
    }

    /**
     * A panel used for displaying the lemma of a context.
     */
    private class ContextPanel extends Panel {
        /**
         * Creates a panel.
         *
         * @param id    ID of the panel
         * @param label label to display
         */
        public ContextPanel(String id, String label) {
            super(id);
            add(new Label("text", label));
        }
    }
}
