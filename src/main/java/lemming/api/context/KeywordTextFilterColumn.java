package lemming.api.context;

import lemming.api.table.TextFilterColumn;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

/**
 * A TextFilteredColumn adding to display keywords of contexts properly.
 *
 * @param <T> data type that is provided
 */
public class KeywordTextFilterColumn<T> extends TextFilterColumn<T,T,String> {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new TextFilterColumn for contexts.
     *
     * @param displayModel
     *            title of a column
     * @param propertyExpression
     *            property expression of a column
     */
    public KeywordTextFilterColumn(IModel<String> displayModel, String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    /**
     * Creates a new TextFilterColumn for contexts.
     *
     * @param displayModel
     *            title of a column
     * @param sortProperty
     *            sort property of a column
     * @param propertyExpression
     *            property expression of a column
     */
    public KeywordTextFilterColumn(IModel<String> displayModel, String sortProperty, String propertyExpression) {
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
        if (rowModel.getObject() instanceof Context) {
            Context context = (Context) rowModel.getObject();
            item.add(new KeywordPanel(componentId, context.getPreceding()))
                    .add(AttributeModifier.append("class", "keyword"));
        } else if (rowModel.getObject() instanceof SelectableContextWrapper) {
            SelectableContextWrapper contextWrapper = (SelectableContextWrapper) rowModel.getObject();
            item.add(new KeywordPanel(componentId, contextWrapper.getContext().getPreceding()))
                    .add(AttributeModifier.append("class", "keyword"));
        }
    }

    /**
     * A panel used for displaying keywords for contexts.
     */
    private class KeywordPanel extends Panel {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a new panel.
         *
         * @param id
         *            ID of the panel
         * @param label
         *            label to display
         */
        public KeywordPanel(String id, String label) {
            super(id);
            add(new Label("keywordText", label));
        }
    }
}
