package lemming.tree;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;

import java.util.Iterator;

/**
 * A subtree handles all children of a single node (or the root nodes if a null node was given to the constructor).
 *
 * @param <T> data type
 */
public class Subtree<T> extends Panel {
    /**
     * The owning tree.
     */
    private AbstractNestedTree<T> tree;

    /**
     * Creates a subtree.
     *
     * @param tree the owning tree
     * @param model model of the node object
     */
    public Subtree(AbstractNestedTree<T> tree, IModel<T> model) {
        super("subtree", model);
        this.tree = tree;
        RefreshingView<T> branches = new RefreshingView<T>("branches") {
            @Override
            protected Iterator<IModel<T>> getItemModels() {
                return new ModelIterator();
            }

            @Override
            protected Item<T> newItem(String id, int index, IModel<T> model) {
                return new Branch<>(id, index, model);
            }

            @Override
            protected void populateItem(Item<T> item) {
                IModel<T> model = item.getModel();
                Component node = tree.newNodeComponent("node", model);

                item.add(node);
                item.add(new Subtree<>(tree, model));
            }
        };

        add(branches);
    }

    /**
     * Returns the model of a node.
     *
     * @return A model or null.
     */
    @SuppressWarnings("unchecked")
    public IModel<T> getModel() {
        return (IModel<T>) getDefaultModel();
    }

    /**
     * Returns the model object of a node.
     *
     * @return A model object or null.
     */
    @SuppressWarnings("unchecked")
    public T getModelObject() {
        return (T) getDefaultModelObject();
    }

    /**
     * Returns wether a subtree is visible.
     *
     * @return True if a node is a root node or its state is set to expanded.
     */
    @Override
    public boolean isVisible() {
        T modelObject = getModelObject();

        // null if there is only a root node
        return modelObject == null || tree.getState(modelObject) == AbstractTree.State.EXPANDED;
    }

    /**
     * Processes the component tag.
     *
     * @param tag tag to modify
     */
    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        tag.put("class", "subtree");
    }

    /**
     * A model wrapper for an iterator.
     */
    private final class ModelIterator implements Iterator<IModel<T>> {
        /**
         * The wrapped iterator.
         */
        private Iterator<? extends T> iterator;

        /**
         * Creates a model iterator.
         */
        public ModelIterator() {
            T modelObject = getModelObject();

            if (modelObject == null) {
                iterator = tree.getProvider().getRoots();
            } else {
                iterator = tree.getProvider().getChildren(modelObject);
            }
        }

        /**
         * Method remove isn’t useful.
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns true if the iteration has more elements.
         *
         * @return True if the iteration has more elements.
         */
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return The next element in the iteration.
         */
        @Override
        public IModel<T> next() {
            return tree.getProvider().model(iterator.next());
        }
    }
}
