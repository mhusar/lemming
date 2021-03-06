package lemming.context;

import lemming.auth.SignInPage;
import lemming.auth.WebSession;
import lemming.table.FilterPanelColumn;
import lemming.ui.panel.ModalMessagePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

/**
 * A custom column with actions for contexts and and a filter panel as filter.
 */
public class ContextActionPanelColumn extends FilterPanelColumn<Context> {
    /**
     * Creates a column.
     *
     * @param displayModel title of the column
     */
    public ContextActionPanelColumn(IModel<String> displayModel) {
        super(displayModel, Context.class);
    }

    /**
     * Returns the CSS class of this type of column.
     *
     * @return A string representing a CSS class.
     */
    @Override
    public String getCssClass() {
        return "actionColumn";
    }

    /**
     * Populates cell items with components.
     *
     * @param cellItem    cell item that is populated
     * @param componentId ID of the child component
     * @param rowModel    model of the row
     */
    @Override
    public void populateItem(Item<ICellPopulator<Context>> cellItem, String componentId, IModel<Context> rowModel) {
        cellItem.add(new ActionPanel(componentId, rowModel));
    }

    /**
     * A panel with actions for context objects.
     */
    private class ActionPanel extends Panel {
        /**
         * Creates a action panel.
         *
         * @param id    ID of the panel
         * @param model context model of a cell item
         */
        public ActionPanel(String id, final IModel<Context> model) {
            super(id, model);

            if (WebSession.get().getUser() == null) {
                setResponsePage(SignInPage.class);
            }

            add(new Link<Void>("editLink") {
                @Override
                public void onClick() {
                    setResponsePage(new ContextEditPage(model, getPage().getPageClass()));
                }
            });
            add(new AjaxLink<Void>("deleteLink") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    ModalMessagePanel contextDeleteConfirmPanel = (ModalMessagePanel) getPage()
                            .get("contextDeleteConfirmPanel");

                    contextDeleteConfirmPanel.show(target, model);
                }
            });
        }
    }
}