package lemming.tree;

import org.apache.wicket.model.IModel;

/**
 * A selectable tree node.
 *
 * @param <T> data type
 */
class Node<T> extends BaseNode<T> {
    /**
     * Creates a tree node.
     *
     * @param id    ID of the node
     * @param tree  the owning tree
     * @param model model of the node object
     */
    public Node(String id, AbstractNestedTree<T> tree, IModel<T> model) {
        super(id, tree, model);
        add(new NodeSwitch("switch"));
        add(tree.newContentComponent("content", getModel()));
        add(new SelectBehavior());
        add(new StyleBehavior());
    }
}
