package lemming.table;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

/**
 * A TextFilteredColumn displaying a badge.
 *
 * @param <T> object type
 * @param <F> filter model type
 * @param <S> sort property type
 */
public abstract class BadgeColumn<T, F, S> extends TextFilterColumn<T, F, S> {
    /**
     * Creates a badge column.
     *
     * @param displayModel       title of a column
     * @param propertyExpression property expression of a column
     */
    public BadgeColumn(IModel<String> displayModel, String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    /**
     * Creates a badge column.
     *
     * @param displayModel       model of a column
     * @param sortProperty       sort property of a column
     * @param propertyExpression property expression of a column
     */
    public BadgeColumn(IModel<String> displayModel, S sortProperty, String propertyExpression) {
        super(displayModel, sortProperty, propertyExpression);
    }

    /**
     * Populates cell items with components.
     * <p>
     * Returns the CSS class of this type of column.
     *
     * @return A string representing a CSS class.
     */
    @Override
    public String getCssClass() {
        return "badgeColumn";
    }

    /**
     * Populates the current table cell item.
     *
     * @param item        item representing the current table cell being rendered
     * @param componentId id of the component used to render the cell
     * @param rowModel    model of the row item being rendered
     */
    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
        Panel badgePanel = createBadgePanel(componentId, rowModel);
        item.add(badgePanel);
    }

    /**
     * Creates a badge panel.
     *
     * @param panelId ID of the panel
     * @param model   model of the row item
     * @return A badge panel.
     */
    public abstract Panel createBadgePanel(String panelId, IModel<T> model);

    /**
     * Called when a link inside a badge panel is clicked.
     *
     * @param target target that produces an Ajax response
     */
    public abstract void onClick(AjaxRequestTarget target);

    /**
     * A panel used to display a badge.
     */
    public class BadgePanel extends Panel {
        /**
         * Creates a panel.
         *
         * @param id    ID of the panel
         * @param label string displayed by label; set parameter to null to make badge invisible
         * @param dummy a dummy string which determines the size of an invisible badge
         */
        public BadgePanel(String id, String label, String dummy) {
            super(id);
            AjaxLink<Void> link = new AjaxLink<Void>("link") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    BadgeColumn.this.onClick(target);
                }

                // prevent event bubbling of click events to make a badge panel usable in selectable rows
                @Override
                protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                    super.updateAjaxAttributes(attributes);
                    attributes.setEventPropagation(AjaxRequestAttributes.EventPropagation.STOP);
                }
            };
            MarkupContainer badge = new WebMarkupContainer("badge");

            if (label != null) {
                badge.add(new Label("label", label));
            } else {
                badge.add(new Label("label", dummy));
                badge.add(AttributeModifier.append("class", "invisible"));
            }

            link.add(badge);
            add(link);
        }
    }
}
