package lemming.tree;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Defines an interface for a drop listener.
 */
public interface IDropListener {
    /**
     * Called when a source component is dropped on a target component’s bottom dropzone.
     *
     * @param target target that produces an Ajax response
     * @param sourceComponent the source component
     * @param targetComponent the target component
     */
    void onBottomDrop(AjaxRequestTarget target, Component sourceComponent, Component targetComponent);

    /**
     * Called when a source component is dropped on a target component’s middle dropzone.
     *
     * @param target target that produces an Ajax response
     * @param sourceComponent the source component
     * @param targetComponent the target component
     */
    @SuppressWarnings({"unused", "EmptyMethod"})
    void onMiddleDrop(AjaxRequestTarget target, Component sourceComponent, Component targetComponent);

    /**
     * Called when a source component is dropped on a target component’s top dropzone..
     *
     * @param target target that produces an Ajax response
     * @param sourceComponent the source component
     * @param targetComponent the target component
     */
    void onTopDrop(AjaxRequestTarget target, Component sourceComponent, Component targetComponent);
}
