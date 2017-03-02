package lemming.tree;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.model.IModel;

/**
 * Folder representation of a tree’s node.
 *
 * @param <T> data type of a node
 */
public class GenericFolder<T> extends Folder<T> {
    /**
     * Tree the folder is be attached to.
     */
    private GenericNestedTree<T> tree;

    /**
     * Creates a folder.
     *
     * @param id ID of a folder
     * @param tree tree the folder is be attached to
     * @param model model of node data
     */
    public GenericFolder(String id, GenericNestedTree<T> tree, IModel<T> model) {
        super(id, tree, model);
        this.tree = tree;
    }

    /**
     * Returns a style class for closed folders.
     *
     * @return A string.
     */
    @Override
    protected String getClosedStyleClass() {
        return "closed";
    }

    /**
     * Returns a style class for open folders.
     *
     * @return A string.
     */
    @Override
    protected String getOpenStyleClass() {
        return "open";
    }

    /**
     * Returns a style class for anything other than closed or open folders.
     *
     * @param sense sense to style
     * @return A string.
     */
    @Override
    protected String getOtherStyleClass(T sense) {
        return "other";
    }

    /**
     * Returns a style class for selected folders.
     *
     * @return A string.
     */
    @Override
    protected String getSelectedStyleClass() {
        return "selected";
    }

    /**
     * Clickable if a node is collapsible.
     *
     * @return Always true.
     */
    @Override
    protected boolean isClickable() {
        return true;
    }

    /**
     * True if a node is selcted.
     *
     * @return True or false.
     */
    @Override
    protected boolean isSelected() {
        if (tree.getSelectedNodeModel() instanceof IModel) {
            if (tree.getSelectedNodeModel().getObject() instanceof Object) {
                return tree.getSelectedNodeModel().getObject().equals(getModelObject()) ? true : false;
            }
        }

        return false;
    }

    /**
     * Toggles a folders state on click.
     *
     * @param target target that produces an Ajax response
     */
    @Override
    protected void onClick(AjaxRequestTarget target) {
        super.onClick(target);
        tree.select(target, getModelObject());
    }
}
