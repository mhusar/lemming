package lemming.ui;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;

import java.util.Iterator;

/**
 * A repeating view which can be refreshed asynchronously.
 *
 * @param <T> data type that is displayed
 */
public abstract class AjaxView<T> extends RepeatingView {
    /**
     * Model of selected data.
     */
    private IModel<T> selectedModel;

    /**
     * Container that is displayed when there are no items available.
     */
    private WebMarkupContainer noItemContainer;

    /**
     * Creates an Ajax view.
     *
     * @param id ID of the view
     */
    protected AjaxView(String id) {
        super(id);
    }

    /**
     * Creates an Ajax view.
     *
     * @param id    ID of the view
     * @param model model of parent data
     */
    public AjaxView(String id, IModel<T> model) {
        super(id, model);
    }

    /**
     * Sets the reference to a container that is displayed if there is no item
     * available.
     *
     * @param noItemContainer a container that is displayed if there are no items available
     */
    public void setNoItemContainer(WebMarkupContainer noItemContainer) {
        this.noItemContainer = noItemContainer;
    }

    /**
     * Returns the selected data model.
     *
     * @return A model of selected data.
     */
    private IModel<T> getSelectedModel() {
        return selectedModel;
    }

    /**
     * Sets the selected data model.
     *
     * @param selectedModel a data model which shall be selected
     */
    public void setSelectedModel(IModel<T> selectedModel) {
        this.selectedModel = selectedModel;
    }

    /**
     * Returns models which shall be displayed as items.
     *
     * @return An iterator for all item models.
     */
    protected abstract Iterator<IModel<T>> getItemModels();

    /**
     * Creates a item wrapping a data model.
     *
     * @param id         ID of the item
     * @param index      index of the item
     * @param model      model wrapped by the item
     * @param isSelected defines if a data model is selected
     * @return An item wrapping a data model.
     */
    protected abstract Item<T> getNewItem(String id, int index, IModel<T> model, Boolean isSelected);

    /**
     * Creates a JavaScript string which refreshes the no item container if
     * evaluated.
     *
     * @param id        ID of the container
     * @param parentId  parent ID of the container
     * @param isVisible visibility state of the container
     * @return A JavaScript string.
     */
    protected abstract String getRefreshNoItemContainerJavaScript(String id, String parentId, Boolean isVisible);

    /**
     * Creates a JavaScript string which refreshes an item if evaluated.
     *
     * @param id         ID of the item
     * @param index      index of the item
     * @param model      model wrapped by the item
     * @param isSelected defines if a data model is selected
     * @return A JavaScript string.
     */
    protected abstract String getRefreshItemJavaScript(String id, int index, IModel<T> model, Boolean isSelected);

    /**
     * Creates a JavaScript string which appends an item if evaluated.
     *
     * @param id         ID of the item
     * @param parentId   parent ID of the item
     * @param index      index of the item
     * @param model      model wrapped by the item
     * @param isSelected defines if a data model is selected
     * @return A JavaScript string.
     */
    protected abstract String getAppendItemJavaScript(String id, String parentId, int index, IModel<T> model,
                                                      Boolean isSelected);

    /**
     * Creates a JavaScript string whiche removes an item if evaluated.
     *
     * @param id ID of the item
     * @return A JavaScript string.
     */
    protected abstract String getRemoveItemJavaScript(String id);

    /**
     * Called on item clicked.
     *
     * @param target target that produces an Ajax response
     * @param model  model of the clicked item
     */
    protected abstract void onItemClick(AjaxRequestTarget target, IModel<T> model);

    /**
     * Called when an Ajax view is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();

        Iterator<IModel<T>> iterator = getItemModels();
        Integer index = 0;

        while (iterator.hasNext()) {
            IModel<T> model = iterator.next();
            Boolean isSelected;
            Item<T> item;

            if (!(selectedModel instanceof IModel)) {
                selectedModel = model;
            }

            isSelected = isSelected(model);
            item = getNewItem(newChildId(), index++, model, isSelected);

            item.setOutputMarkupId(true);
            addOnClickBehavior(item);
            add(item);
        }
    }

    /**
     * Called when an Ajax view is configured.
     */
    @Override
    protected void onConfigure() {
        if (noItemContainer != null) {
            if (size() > 0) {
                noItemContainer.setVisible(false);
            } else {
                noItemContainer.setVisible(true);
            }
        }

        super.onConfigure();
    }

    /**
     * Returns an iterator for the collection of child components to be
     * rendered.
     *
     * @return An iterator for child components.
     */
    @Override
    protected Iterator<? extends Component> renderIterator() {
        return iterator();
    }

    /**
     * Checks if a data model is selected.
     *
     * @param model model that is checked
     * @return True if the model is selected; false otherwise.
     */
    private Boolean isSelected(IModel<T> model) {
        return model.getObject().equals(getSelectedModel().getObject());
    }

    /**
     * Adds a behaviour to respond to click events.
     *
     * @param item the item a behavior is added to
     */
    private void addOnClickBehavior(final Item<T> item) {
        item.add(new AjaxEventBehavior("click") {
            /**
             * Listener for an Ajax event.
             *
             * @param target
             *            target that produces an Ajax response
             */
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                selectedModel = item.getModel();

                onItemClick(target, selectedModel);
                refresh(target);
            }
        });
    }

    /**
     * Refreshes child components with item models.
     *
     * @param target target that produces an Ajax response
     */
    @SuppressWarnings("unchecked")
    public void refresh(AjaxRequestTarget target) {
        Iterator<Component> childIterator = iterator();
        Iterator<IModel<T>> modelIterator = getItemModels();
        Integer index = 0;

        while (modelIterator.hasNext()) {
            IModel<T> model = modelIterator.next();
            Boolean isSelected = isSelected(model);

            if (childIterator.hasNext()) {
                Item<T> item = (Item<T>) childIterator.next();

                item.setIndex(index);
                item.setModel(model);
                refreshItem(target, item, isSelected);
            } else {
                Item<T> item = getNewItem(newChildId(), index, model, isSelected);

                childIterator = null;
                appendItem(target, item, isSelected);
            }

            index++;
        }

        if (childIterator != null) {
            while (childIterator.hasNext()) {
                removeItem(target, (Item<T>) childIterator.next());
            }
        }

        refreshNoItemContainer(target);
    }

    /**
     * @param target target that produces an Ajax response
     */
    private void refreshNoItemContainer(AjaxRequestTarget target) {
        if (noItemContainer != null) {
            if (size() > 0) {
                noItemContainer.setVisible(false);
            } else {
                noItemContainer.setVisible(true);
            }

            String javaScript = getRefreshNoItemContainerJavaScript(
                    noItemContainer.getMarkupId(), noItemContainer.getParent()
                            .getMarkupId(), noItemContainer.isVisible());

            target.prependJavaScript(javaScript);
            target.add(noItemContainer);
        }
    }

    /**
     * Refreshes a child item.
     *
     * @param target     target that produces an Ajax response
     * @param item       the item to refresh
     * @param isSelected defines if a data model is selected
     */
    private void refreshItem(AjaxRequestTarget target, Item<T> item, Boolean isSelected) {
        replace(item);
        String javaScript = getRefreshItemJavaScript(item.getMarkupId(), item.getIndex(), item.getModel(), isSelected);
        target.prependJavaScript(javaScript);
    }

    /**
     * Appends a child item.
     *
     * @param target     target that produces an Ajax response
     * @param item       the item to append
     * @param isSelected defines if a data model is selected
     */
    private void appendItem(AjaxRequestTarget target, Item<T> item, Boolean isSelected) {
        item.setOutputMarkupId(true);
        addOnClickBehavior(item);
        add(item);

        String javaScript = getAppendItemJavaScript(item.getMarkupId(), getParent().getMarkupId(), item.getIndex(),
                item.getModel(), isSelected);

        target.prependJavaScript(javaScript);
        target.add(item);
    }

    /**
     * Removes a child item.
     *
     * @param target target that produces an Ajax response
     * @param item   the item to remove
     */
    private void removeItem(AjaxRequestTarget target, Item<T> item) {
        remove(item);
        String javaScript = getRemoveItemJavaScript(item.getMarkupId());
        target.prependJavaScript(javaScript);
    }
}
