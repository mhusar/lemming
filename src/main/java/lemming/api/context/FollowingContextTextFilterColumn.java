package lemming.api.context;

import lemming.api.table.TextFilterColumn;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

/**
 * A TextFilteredColumn adding to display following contexts properly.
 */
public class FollowingContextTextFilterColumn extends TextFilterColumn<Context,Context,String> {
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
     * Creates a new TextFilterColumn for following contexts.
     *
     * @param displayModel
     *            title of a column
     * @param propertyExpression
     *            property expression of a column
     */
    public FollowingContextTextFilterColumn(IModel<String> displayModel, String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    /**
     * Creates a new TextFilterColumn for following contexts.
     *
     * @param displayModel
     *            title of a column
     * @param propertyExpression
     *            property expression of a column
     * @param maxLength
     *            maximum length
     */
    public FollowingContextTextFilterColumn(IModel<String> displayModel, String propertyExpression, Integer maxLength) {
        super(displayModel, propertyExpression);
        this.maxLength = maxLength;
    }

    /**
     * Creates a new TextFilterColumn for following contexts.
     *
     * @param displayModel
     *            title of a column
     * @param sortProperty
     *            sort property of a column
     * @param propertyExpression
     *            property expression of a column
     */
    public FollowingContextTextFilterColumn(IModel<String> displayModel, String sortProperty, String propertyExpression) {
        super(displayModel, sortProperty, propertyExpression);
    }

    /**
     * Creates a new TextFilterColumn for following contexts.
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
    public FollowingContextTextFilterColumn(IModel<String> displayModel, String sortProperty, String propertyExpression,
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
        item.add(new ContextPanel(componentId, context.getFollowing()))
                .add(AttributeModifier.append("class", "following"));
    }

    /**
     * A panel used for displaying keywords for following contexts.
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
            String trimmedLabel = label.length() > maxLength ?
                    label.substring(0, Math.min(label.length(), maxLength)).trim() + "…" : label.trim();
            add(new Label("contextText", trimmedLabel).add(AttributeModifier.append("title",
                    label.trim())));
        }
    }
}
