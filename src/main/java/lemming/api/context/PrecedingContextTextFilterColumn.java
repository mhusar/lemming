package lemming.api.context;

import lemming.api.table.TextFilterColumn;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

/**
 * A TextFilteredColumn adding to display preceding contexts properly.
 */
public class PrecedingContextTextFilterColumn extends TextFilterColumn<Context,Context,String> {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default maximum length of a following context.
     */
    private static final Integer DEFAULT_MAX_LENGTH = 25;

    /**
     * Maximum length of a following context.
     */
    private Integer maxLength = DEFAULT_MAX_LENGTH;

    /**
     * Creates a new TextFilterColumn for preceding contexts.
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
     * Creates a new TextFilterColumn for preceding contexts.
     *
     * @param displayModel
     *            title of a column
     * @param propertyExpression
     *            property expression of a column
     * @param maxLength
     *            maximum length
     */
    public PrecedingContextTextFilterColumn(IModel<String> displayModel, String propertyExpression,
                                            Integer maxLength) {
        super(displayModel, propertyExpression);
        this.maxLength = maxLength;
    }

    /**
     * Creates a new TextFilterColumn for preceding contexts.
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
     * Creates a new TextFilterColumn for preceding contexts.
     *
     * @param displayModel
     *            title of a column
     * @param sortProperty
     *            sort property of a column
     * @param propertyExpression
     *            property expression of a column
     * @param maxLength
     *            maximum length
     */
    public PrecedingContextTextFilterColumn(IModel<String> displayModel, String sortProperty, String propertyExpression,
                                            Integer maxLength) {
        super(displayModel, sortProperty, propertyExpression);
        this.maxLength = maxLength;
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
        item.add(new ContextPanel(componentId, context.getPreceding()))
                .add(AttributeModifier.append("class", "preceding"));
    }

    /**
     * Returns the header of a column.
     *
     * @param componentId id of the component used to render the cell
     * @return A header component.
     */
    @Override
    public Component getHeader(String componentId) {
        return super.getHeader(componentId).add(AttributeModifier.append("class", "preceding"));
    }

    /**
     * A panel used for displaying keywords for preceding contexts.
     */
    private class ContextPanel extends Panel {
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
        public ContextPanel(String id, String label) {
            super(id);
            Integer beginIndex = label.length() > maxLength ? label.length() - maxLength : 0;
            String trimmedLabel = label.length() > maxLength ? "…" + label.substring(beginIndex).trim() : label.trim();
            add(new Label("contextText", trimmedLabel).add(AttributeModifier.append("title",
                    label.trim())));
        }
    }
}
