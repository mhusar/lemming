package lemming.sense;

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
import org.apache.wicket.model.Model;

/**
 * A custom column with actions for senses and and a filter panel as filter.
 */
public class SenseActionPanelColumn extends FilterPanelColumn<Sense> {
    /**
     * Creates a column.
     *
     * @param displayModel title of the column
     */
    public SenseActionPanelColumn(IModel<String> displayModel) {
        super(displayModel, Sense.class);
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
    public void populateItem(Item<ICellPopulator<Sense>> cellItem, String componentId, IModel<Sense> rowModel) {
        cellItem.add(new ActionPanel(componentId, rowModel));
    }

    /**
     * A panel with actions for sense objects.
     */
    private class ActionPanel extends Panel {
        /**
         * Creates a action panel.
         *
         * @param id    ID of the panel
         * @param model sense model of a cell item
         */
        public ActionPanel(String id, final IModel<Sense> model) {
            super(id, model);

            if (WebSession.get().getUser() == null) {
                setResponsePage(SignInPage.class);
            }

            add(new Link<Void>("editLink") {
                @Override
                public void onClick() {
                    setResponsePage(new SenseEditPage(model));
                }
            });
            add(new AjaxLink<Void>("deleteLink") {
                /**
                 * Called when an AjaxLink is configured.
                 */
                @Override
                protected void onConfigure() {
                    super.onConfigure();
                    Sense sense = model.getObject();

                    if (sense.isParentSense() && new SenseDao().hasChildSenses(sense)) {
                        setVisible(false);
                    } else {
                        setVisible(true);
                    }
                }

                /**
                 * Called on click.
                 *
                 * @param target target that produces an Ajax response
                 */
                @Override
                public void onClick(AjaxRequestTarget target) {
                    ModalMessagePanel senseDeleteConfirmPanel = (ModalMessagePanel) getPage()
                            .get("senseDeleteConfirmPanel");
                    senseDeleteConfirmPanel.show(target, new Model<>(model.getObject()));
                }
            });
        }
    }
}
