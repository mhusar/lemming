package lemming.context;

import lemming.table.TextFilterColumn;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

/**
 * A TextFilteredColumn to display preceding contexts properly.
 */
public class PrecedingContextTextFilterColumn extends TextFilterColumn<Context,Context,String> {
    /**
     * Creates a TextFilterColumn for preceding contexts.
     *
     * @param displayModel
     *            title of a column
     * @param propertyExpression
     *            property expression of a column
     */
    public PrecedingContextTextFilterColumn(IModel<String> displayModel, String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    /**
     * Creates a TextFilterColumn for preceding contexts.
     *
     * @param displayModel
     *            title of a column
     * @param sortProperty
     *            sort property of a column
     * @param propertyExpression
     *            property expression of a column
     */
    public PrecedingContextTextFilterColumn(IModel<String> displayModel, String sortProperty,
                                            String propertyExpression) {
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
    public void populateItem(Item<ICellPopulator<Context>> item, String componentId, IModel<Context> rowModel) {
        Context context = rowModel.getObject();
        item.add(new ContextPanel(componentId, context.getPreceding(), context.getInitPunctuation()))
                .add(AttributeModifier.append("class", "preceding auto-shrink auto-shrink-left"));
    }

    /**
     * A panel used for displaying preceding text.
     */
    private class ContextPanel extends Panel {
        /**
         * Creates a panel.
         *
         * @param id
         *            ID of the panel
         * @param preceding
         *            preceding text to display
         * @param punctuation
         *            preceding punctuation of keyword
         */
        public ContextPanel(String id, String preceding, String punctuation) {
            super(id);
            String label = "<span class='string'>" + preceding + "</span>";
            String titleString = preceding.trim();

            if (punctuation != null) {
                label = label + " <span class='punctuation'>" + punctuation + "</span>";
                titleString = titleString + " " + punctuation;
            }

            add(new Label("contextText", label.trim())
                    .add(AttributeModifier.append("title", titleString)).setEscapeModelStrings(false));
        }
    }
}
