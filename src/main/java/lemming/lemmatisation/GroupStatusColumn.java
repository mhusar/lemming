package lemming.lemmatisation;

import lemming.context.Context;
import lemming.context.ContextType;
import lemming.table.TextFilterColumn;
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
 * A TextFilteredColumn displaying the group status of a context.
 */
public abstract class GroupStatusColumn extends TextFilterColumn<Context, Context, String> {
    /**
     * Creates a context group status column.
     *
     * @param displayModel       title of a column
     * @param propertyExpression property expression of a column
     */
    public GroupStatusColumn(IModel<String> displayModel, String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    /**
     * Creates a context group status column.
     *
     * @param displayModel       model of a column
     * @param sortProperty       sort property of a column
     * @param propertyExpression property expression of a column
     */
    public GroupStatusColumn(IModel<String> displayModel, String sortProperty, String propertyExpression) {
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
        return "groupStatusColumn";
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
        Panel groupPanel = createGroupPanel(componentId, rowModel);
        item.add(groupPanel);
    }

    /**
     * Creates a context group status panel.
     *
     * @param panelId ID of the panel
     * @param model   model of the row item
     * @return A context group panel.
     */
    public abstract Panel createGroupPanel(String panelId, IModel<Context> model);

    /**
     * Called when a link inside a context group status panel is clicked.
     *
     * @param target target that produces an Ajax response
     * @param model model of the row item
     */
    public abstract void onClick(AjaxRequestTarget target, IModel<Context> model);

    /**
     * A panel used to display the group status of a context.
     */
    public class GroupStatusPanel extends Panel {
        /**
         * Creates a panel.
         *
         * @param id    ID of the panel
         * @param model default model of the panel
         */
        public GroupStatusPanel(String id, IModel<Context> model) {
            super(id, model);
            AjaxLink<Void> link = new AjaxLink<Void>("link") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    GroupStatusColumn.this.onClick(target, model);
                }

                // prevent event bubbling of click events to make a context group status panel usable in selectable rows
                @Override
                protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                    super.updateAjaxAttributes(attributes);
                    attributes.setEventPropagation(AjaxRequestAttributes.EventPropagation.STOP);
                }
            };
            MarkupContainer group = new WebMarkupContainer("status");

            if (model.getObject().getType().equals(ContextType.Type.GROUP)) {
                group.add(new Label("label", "G"));
            } else if (model.getObject().getGrouped()) {
                group.add(new Label("label", "M"));
            } else {
                group.add(new Label("label", ""));
            }

            link.add(group);
            add(link);
        }
    }
}
