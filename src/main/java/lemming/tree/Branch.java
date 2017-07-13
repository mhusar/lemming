package lemming.tree;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

/**
 * A container for a node and its children.
 *
 * @param <T> data type
 */
class Branch<T> extends Item<T> {
    /**
     * Creates a branch item.
     *
     * @param id    ID of the branch
     * @param index index of the branch
     * @param model model of the branch
     */
    public Branch(String id, int index, IModel<T> model) {
        super(id, index, model);
        setOutputMarkupId(true);
    }

    /**
     * Processes the component tag.
     *
     * @param tag tag to modify
     */
    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        String styleClass;

        if (isFirst()) {
            styleClass = "branch-first";
        } else if (isLast()) {
            styleClass = "branch-last";
        } else {
            styleClass = "branch-middle";
        }

        tag.put("class", "branch " + styleClass);
    }

    /**
     * Checks if a branch is first in its parent container.
     *
     * @return True or false.
     */
    private boolean isFirst() {
        return getIndex() == 0;
    }

    /**
     * Checks if a branch is last in its parent container.
     *
     * @return True or false.
     */
    private boolean isLast() {
        return getIndex() == getParent().size() - 1;
    }
}
