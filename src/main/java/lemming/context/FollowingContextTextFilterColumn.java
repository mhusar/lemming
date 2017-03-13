package lemming.context;

import lemming.table.TextFilterColumn;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

/**
 * A TextFilteredColumn to display following contexts properly.
 */
public class FollowingContextTextFilterColumn extends TextFilterColumn<Context,Context,String> {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a TextFilterColumn for following contexts.
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
     * Creates a TextFilterColumn for following contexts.
     *
     * @param displayModel
     *            title of a column
     * @param sortProperty
     *            sort property of a column
     * @param propertyExpression
     *            property expression of a column
     */
    public FollowingContextTextFilterColumn(IModel<String> displayModel, String sortProperty,
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
        item.add(new ContextPanel(componentId, context.getFollowing(), context.getPunctuation()))
                .add(AttributeModifier.append("class", "following auto-shrink"));
    }

    /**
     * A panel used for displaying following text.
     */
    private class ContextPanel extends Panel {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a panel.
         *
         * @param id
         *            ID of the panel
         * @param following
         *            following to display
         * @param punctuation
         *            punctuation of keyword
         */
        public ContextPanel(String id, String following, String punctuation) {
            super(id);
            String label = following.trim();
            String titleString = following.trim();

            if (punctuation instanceof String) {
                label = "<span class='punctuation'>" + punctuation + "</span>" + label;
                titleString = punctuation + titleString;
            }

            add(new Label("contextText", label)
                    .add(AttributeModifier.append("title", titleString)).setEscapeModelStrings(false));
        }
    }
}
