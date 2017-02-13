package lemming.sense;

import lemming.auth.WebSession;
import lemming.table.FilterPanelColumn;
import lemming.ui.panel.ModalMessagePanel;
import lemming.user.User;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * A custom column with actions for senses and and a filter panel as filter.
 */
public class SenseActionPanelColumn extends FilterPanelColumn<SenseWrapper> {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a column.
     * 
     * @param displayModel
     *            title of the column
     */
    public SenseActionPanelColumn(IModel<String> displayModel) {
        super(displayModel, SenseWrapper.class);
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
     * @param cellItem
     *            cell item that is populated
     * @param componentId
     *            ID of the child component
     * @param rowModel
     *            model of the row
     */
    @Override
    public void populateItem(Item<ICellPopulator<SenseWrapper>> cellItem, String componentId, IModel<SenseWrapper> rowModel) {
        cellItem.add(new ActionPanel(componentId, rowModel));
    }

    /**
     * A panel with actions for sense objects.
     */
    private class ActionPanel extends Panel {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a action panel.
         * 
         * @param id
         *            ID of the panel
         * @param model
         *            sense model of a cell item
         */
        public ActionPanel(String id, final IModel<SenseWrapper> model) {
            super(id, model);
            User sessionUser = WebSession.get().getUser();

            if (sessionUser instanceof User) {
                add(new AjaxLink<Void>("editLink") {
                    /**
                     * Determines if a deserialized file is compatible with
                     * this class.
                     */
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        setResponsePage(new SenseEditPage(model, getPage().getPageClass()));
                    }
                });
                add(new AjaxLink<Void>("deleteLink") {
                    /**
                     * Determines if a deserialized file is compatible with
                     * this class.
                     */
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        ModalMessagePanel senseDeleteConfirmPanel = (ModalMessagePanel) getPage()
                                .get("senseDeleteConfirmPanel");
                        ModalMessagePanel senseDeleteDeniedPanel = (ModalMessagePanel) getPage()
                                .get("senseDeleteDeniedPanel");

                        // TODO: When to show a sense delete deny panel?
                        //senseDeleteDeniedPanel.show(target, model);
                        senseDeleteConfirmPanel.show(target, new Model<Sense>(model.getObject().getSense()));
                    }
                });
            }
        }
    }
}