package lemming.tree;

import org.apache.wicket.Component;

/**
 * Defines an interface for a drop listener.
 */
public interface IDropListener {
    /**
     * Called when a source component is dropped on a target component.
     *
     * @param source the source component
     * @param target the target component
     */
    void onDrop(Component source, Component target);
}
