package lemming.tree;

import org.apache.wicket.Component;

/**
 * Defines an interface for a drop listener.
 */
public interface IDropListener {
    /**
     * Called when a source component is dropped on a target component’s bottom dropzone.
     *
     * @param source the source component
     * @param target the target component
     */
    void onBottomDrop(Component source, Component target);

    /**
     * Called when a source component is dropped on a target component’s middle dropzone.
     *
     * @param source the source component
     * @param target the target component
     */
    void onMiddleDrop(Component source, Component target);

    /**
     * Called when a source component is dropped on a target component’s top dropzone..
     *
     * @param source the source component
     * @param target the target component
     */
    void onTopDrop(Component source, Component target);
}
