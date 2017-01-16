package lemming.api.lemma;

import lemming.api.data.Source;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

import lemming.api.auth.WebSession;
import lemming.api.table.FilterPanelColumn;
import lemming.api.ui.panel.ModalMessagePanel;
import lemming.api.user.User;

/**
 * A custom column with actions for lemmata and and a filter panel as filter.
 */
public class LemmaActionPanelColumn extends FilterPanelColumn<Lemma> {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new column.
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
         * Creates a new action panel.
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

                        // TODO: When to show a lemma delete deny panel?
                        //lemmaDeleteDeniedPanel.show(target, model);
                        lemmaDeleteConfirmPanel.show(target, model);
                    }
                };
                AjaxLink<Void> viewLink = new AjaxLink<Void>("viewLink") {
                    /**
                     * Determines if a deserialized file is compatible with
                     * this class.
                     */
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        setResponsePage(new LemmaViewPage(model));
                    }
                };

                add(editLink);
                add(deleteLink);
                add(viewLink);

                if (model.getObject().getSource().equals(Source.LemmaType.TL)) {
                    editLink.setVisible(false);
                    deleteLink.setVisible(false);
                } else {
                    viewLink.setVisible(false);
                }
            }
        }
    }
}