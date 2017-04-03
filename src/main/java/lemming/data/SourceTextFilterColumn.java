package lemming.data;

import lemming.lemma.Lemma;
import lemming.pos.Pos;
import lemming.table.TextFilterColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

/**
 * A TextFilteredColumn adding to display values of source enums properly.
 *
 * @param <T>
 *            object type
 * @param <F>
 *            filter model type
 * @param <S>
 *            sort property type
 */
public class SourceTextFilterColumn<T,F,S> extends TextFilterColumn<T,F,S> {
    /**
     * Creates a TextFilterColumn for source enums.
     *
     * @param displayModel
     *            title of a column
     * @param propertyExpression
     *            property expression of a column
     */
    public SourceTextFilterColumn(IModel<String> displayModel, String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    /**
     * Creates a TextFilterColumn for source enums.
     *
     * @param displayModel
     *            title of a column
     * @param sortProperty
     *            sort property of a column
     * @param propertyExpression
     *            property expression of a column
     */
    public SourceTextFilterColumn(IModel<String> displayModel, S sortProperty, String propertyExpression) {
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

        if (object instanceof Lemma) {
            Source.LemmaType source = ((Lemma) object).getSource();
            item.add(new SourcePanel(componentId, new StringResourceModel("LemmaType." + source.name())));
        } else if (object instanceof Pos) {
            Source.PosType source = ((Pos) object).getSource();
            item.add(new SourcePanel(componentId, new StringResourceModel("PosType." + source.name())));
        }
    }

    /**
     * A panel used for displaying text for source enums.
     */
    private class SourcePanel extends Panel {
        /**
         * Creates a panel.
         *
         * @param id
         *            ID of the panel
         * @param model
         *            string resource model to display
         */
        public SourcePanel(String id, StringResourceModel model) {
            super(id);
            add(new Label("enumText", model));
        }
    }
}
