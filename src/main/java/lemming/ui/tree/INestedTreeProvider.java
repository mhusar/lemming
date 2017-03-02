package lemming.ui.tree;

import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;

/**
 * A tree provider for a nested tree.
 * @see AbstractNestedTree
 */
public interface INestedTreeProvider<T> extends ITreeProvider<T> {
    /**
     * Checks if a node object has a parent node object.
     *
     * @param node a node object
     * @return True if a node object has a parent.
     */
    boolean hasParent(T node);

    /**
     * Returns the parent node object of a node object.
     *
     * @param node a node object
     * @return A parent node object or null.
     */
    T getParent(T node);
}
