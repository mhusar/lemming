package lemming.lemma;

import lemming.context.ContextDao;
import lemming.data.Source;
import lemming.sense.SenseDao;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

import lemming.auth.WebSession;
import lemming.table.FilterPanelColumn;
import lemming.ui.panel.ModalMessagePanel;
import lemming.user.User;

/**
 * A custom column with actions for lemmata and and a filter panel as filter.
 */
public class LemmaActionPanelColumn extends FilterPanelColumn<Lemma> {
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
    public LemmaActionPanelColumn(IModel<String> displayModel) {
        super(displayModel, Lemma.class);
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
    public void populateItem(Item<ICellPopulator<Lemma>> cellItem, String componentId, IModel<Lemma> rowModel) {
        cellItem.add(new ActionPanel(componentId, rowModel));
    }

    /**
     * A panel with actions for lemma objects.
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
         *            lemma model of a cell item
         */
        public ActionPanel(String id, final IModel<Lemma> model) {
            super(id, model);
            User sessionUser = WebSession.get().getUser();

            if (sessionUser instanceof User) {
                AjaxLink<Void> editLink = new AjaxLink<Void>("editLink") {
                    /**
                     * Determines if a deserialized file is compatible with
                     * this class.
                     */
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        setResponsePage(new LemmaEditPage(model, getPage().getPageClass()));
                    }
                };
                AjaxLink<Void> deleteLink = new AjaxLink<Void>("deleteLink") {
                    /**
                     * Determines if a deserialized file is compatible with
                     * this class.
                     */
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        ModalMessagePanel lemmaDeleteConfirmPanel = (ModalMessagePanel) getPage()
                                .get("lemmaDeleteConfirmPanel");
                        ModalMessagePanel lemmaDeleteDeniedPanel = (ModalMessagePanel) getPage()
                                .get("lemmaDeleteDeniedPanel");

                        if (new ContextDao().findByLemma(model.getObject()).isEmpty() &&
                                new SenseDao().findByLemma(model.getObject()).isEmpty()) {
                            lemmaDeleteConfirmPanel.show(target, model);
                        } else {
                            lemmaDeleteDeniedPanel.show(target, model);
                        }
                    }
                };

                add(editLink);
                add(deleteLink);

                if (model.getObject().getSource().equals(Source.LemmaType.TL)) {
                    deleteLink.setVisible(false);
                }
            }
        }
    }
}