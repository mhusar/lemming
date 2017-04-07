package lemming.tree;

import java.util.Set;

/**
 * Interface for a tree with draggable nodes.
 */
public interface IDraggableTree {
    /**
     * Adds a drop listener to a tree.
     *
     * @param listener a drop listener
     */
    @SuppressWarnings("unused")
    void addDropListener(IDropListener listener);

    /**
     * Returns a set of drop listeners.
     *
     * @return A set of drop listeners.
     */
    Set<? extends IDropListener> getDropListeners();
}
