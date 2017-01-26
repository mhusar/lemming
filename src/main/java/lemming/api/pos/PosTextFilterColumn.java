package lemming.api.pos;

import lemming.api.context.Context;
import lemming.api.table.TextFilterColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

/**
 * A TextFilteredColumn adding to display names of parts of speech properly.
 *
 * @param <T>
 *            object type
 * @param <F>
 *            filter model type
 * @param <S>
 *            sort property type
 */
public class PosTextFilterColumn<T,F,S> extends TextFilterColumn<T,F,S> {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new TextFilterColumn for parts of speech.
     *
     * @param displayModel
     *            title of a column
     * @param propertyExpression
     *            property expression of a column
     */
    public PosTextFilterColumn(IModel<String> displayModel, String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    /**
     * Creates a new TextFilterColumn for parts of speech.
     *
     * @param displayModel
     *            title of a column
     * @param sortProperty
     *            sort property of a column
     * @param propertyExpression
     *            property expression of a column
     */
    public PosTextFilterColumn(IModel<String> displayModel, S sortProperty, String propertyExpression) {
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
        Object object = rowModel.getObject();

        if (object instanceof Context) {
            Pos pos = ((Context) object).getPos();

            if (pos instanceof  Pos) {
                String name = pos.getName();
                item.add(new PosPanel(componentId, name));
            } else {
                item.add(new PosPanel(componentId, ""));
            }
        } else if (object instanceof Pos) {
            String name = ((Pos) object).getName();
            item.add(new PosPanel(componentId, name));
        }
    }

    /**
     * A panel used for displaying text for parts of speech.
     */
    private class PosPanel extends Panel {
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
        public PosPanel(String id, String label) {
            super(id);
            add(new Label("posText", label));
        }
    }
}
