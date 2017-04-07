package lemming.pos;

import lemming.auth.SignInPage;
import lemming.auth.WebSession;
import lemming.context.ContextDao;
import lemming.lemma.LemmaDao;
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
 * A custom column with actions for parts of speech and and a filter panel as filter.
 */
public class PosActionPanelColumn extends FilterPanelColumn<Pos> {
    /**
     * Creates a column.
     * 
     * @param displayModel
     *            title of the column
     */
    public PosActionPanelColumn(IModel<String> displayModel) {
        super(displayModel, Pos.class);
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
    public void populateItem(Item<ICellPopulator<Pos>> cellItem, String componentId, IModel<Pos> rowModel) {
        cellItem.add(new ActionPanel(componentId, rowModel));
    }

    /**
     * A panel with actions for part of speech objects.
     */
    private class ActionPanel extends Panel {
        /**
         * Creates a action panel.
         * 
         * @param id
         *            ID of the panel
         * @param model
         *            part of speech model of a cell item
         */
        public ActionPanel(String id, final IModel<Pos> model) {
            super(id, model);

            if (WebSession.get().getUser() == null) {
                setResponsePage(SignInPage.class);
            }

            add(new Link<Void>("editLink") {
                @Override
                public void onClick() {
                    setResponsePage(new PosEditPage(model, getPage().getPageClass()));
                }
            });
            add(new AjaxLink<Void>("deleteLink") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    ModalMessagePanel posDeleteConfirmPanel = (ModalMessagePanel) getPage()
                            .get("posDeleteConfirmPanel");
                    ModalMessagePanel posDeleteDeniedPanel = (ModalMessagePanel) getPage()
                            .get("posDeleteDeniedPanel");

                    if (new ContextDao().findByPos(model.getObject()).isEmpty() &&
                            new LemmaDao().findByPos(model.getObject()).isEmpty()) {
                        posDeleteConfirmPanel.show(target, model);
                    } else {
                        posDeleteDeniedPanel.show(target, model);
                    }
                }
            });
        }
    }
}