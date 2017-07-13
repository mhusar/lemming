package lemming.context;

import lemming.table.TextFilterColumn;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

/**
 * A TextFilteredColumn adding to display keywords of contexts properly.
 */
public class KeywordTextFilterColumn extends TextFilterColumn<Context, Context, String> {
    /**
     * Creates a TextFilterColumn for contexts.
     *
     * @param displayModel       title of a column
     * @param propertyExpression property expression of a column
     */
    public KeywordTextFilterColumn(IModel<String> displayModel, String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    /**
     * Creates a TextFilterColumn for contexts.
     *
     * @param displayModel       title of a column
     * @param sortProperty       sort property of a column
     * @param propertyExpression property expression of a column
     */
    public KeywordTextFilterColumn(IModel<String> displayModel, String sortProperty, String propertyExpression) {
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
        item.add(new KeywordPanel(componentId, context.getKeyword()))
                .add(AttributeModifier.append("class", "keyword"));
    }

    /**
     * A panel used for displaying keywords for contexts.
     */
    private class KeywordPanel extends Panel {
        /**
         * Creates a panel.
         *
         * @param id    ID of the panel
         * @param label label to display
         */
        public KeywordPanel(String id, String label) {
            super(id);
            add(new Label("keywordText", label));
        }
    }
}
