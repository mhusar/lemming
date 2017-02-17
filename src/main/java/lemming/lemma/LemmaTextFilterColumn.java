package lemming.lemma;

import lemming.sense.SenseWrapper;
import lemming.table.TextFilterColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

/**
 * A TextFilteredColumn adding to display names of lemmata properly.
 *
 * @param <T>
 *            object type
 * @param <F>
 *            filter model type
 * @param <S>
 *            sort property type
 */
public class LemmaTextFilterColumn<T,F,S> extends TextFilterColumn<T,F,S> {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a TextFilterColumn for lemmata.
     *
     * @param displayModel
     *            title of a column
     * @param propertyExpression
     *            property expression of a column
     */
    public LemmaTextFilterColumn(IModel<String> displayModel, String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    /**
     * Creates a TextFilterColumn for lemmata.
     *
     * @param displayModel
     *            title of a column
     * @param sortProperty
     *            sort property of a column
     * @param propertyExpression
     *            property expression of a column
     */
    public LemmaTextFilterColumn(IModel<String> displayModel, S sortProperty, String propertyExpression) {
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

        if (object instanceof SenseWrapper) {
            String name = ((SenseWrapper) object).getLemma().getName();
            item.add(new LemmaPanel(componentId, name));
        }
    }

    /**
     * A panel used for displaying text for lemmata.
     */
    private class LemmaPanel extends Panel {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a panel.
         *
         * @param id
         *            ID of the panel
         * @param label
         *            label to display
         */
        public LemmaPanel(String id, String label) {
            super(id);
            add(new Label("lemmaText", label));
        }
    }
}
