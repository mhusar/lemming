package lemming.tree;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Request;
import org.apache.wicket.util.string.StringValue;

/**
 * A selectable and draggable tree node.
 *
 * @param <T> data type
 */
public class DraggableNode<T> extends Node<T> {
    /**
     * Creates a draggable tree node.
     *
     * @param id ID of the node
     * @param tree the owning tree
     * @param model model of the node object
     */
    public DraggableNode(String id, AbstractNestedTree<T> tree, IModel<T> model) {
        super(id, tree, model);
        add(new DragStartBehavior());
        add(new DragEndBehavior());
        add(new DragoverBehavior());
        add(new DropBehavior());
    }

    /**
     * Processes the component tag.
     *
     * @param tag tag to modify
     */
    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        tag.put("draggable", "true");
    }

    /**
     * A behavior which fires on drag start.
     */
    private class DragStartBehavior extends AjaxEventBehavior {
        /**
         * Creates a drag start behavior.
         */
        public DragStartBehavior() {
            super("dragstart");
        }

        /**
         * Called when a node is starting to drag.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        protected void onEvent(AjaxRequestTarget target) {
            String javaScript = "jQuery('#" + getComponent().getMarkupId() + "').addClass('dragging');";
            target.appendJavaScript(javaScript);
        }

        /**
         * Renders to the web response what the component wants to contribute.
         *
         * @param response response object
         */
        @Override
        public void renderHead(Component component, IHeaderResponse response) {
            super.renderHead(component, response);
            String javaScript = "jQuery(document).on('dragstart', '#" + getMarkupId() + "', function (event) { " +
                    "event.originalEvent.dataTransfer.setData('text/plain', '" + getPageRelativePath() + "'); " +
                    "event.originalEvent.dataTransfer.dropEffect = 'move'; " +
                    "event.originalEvent.dataTransfer.effectAllowed = 'move'; });";
            response.render(OnDomReadyHeaderItem.forScript(javaScript));
        }
    }

    /**
     * A behavior which fires on drag end.
     */
    private class DragEndBehavior extends AjaxEventBehavior {
        /**
         * Creates a drag end behavior.
         */
        public DragEndBehavior() {
            super("dragend");
        }

        /**
         * Called on drag end.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        protected void onEvent(AjaxRequestTarget target) {
            String javaScript = "jQuery('#" + getComponent().getMarkupId() + "').removeClass('dragging');";
            target.appendJavaScript(javaScript);
        }
    }

    /**
     * A behavior which fires on drag over.
     */
    private class DragoverBehavior extends AjaxEventBehavior {
        /**
         * Creates a drag over behavior.
         */
        public DragoverBehavior() {
            super("dragover");
        }

        /**
         * Renders to the web response what the component wants to contribute.
         *
         * @param response response object
         */
        @Override
        public void renderHead(Component component, IHeaderResponse response) {
            super.renderHead(component, response);
            String javaScript = "jQuery(document).on('dragover', '#" + getMarkupId() + "', function (event) { " +
                    "event.preventDefault(); });";
            response.render(OnDomReadyHeaderItem.forScript(javaScript));
        }

        /**
         * Called when a draggable is dragged over a node.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        protected void onEvent(AjaxRequestTarget target) {
        }
    }

    /**
     * A behavior which fires on drop.
     */
    private class DropBehavior extends AjaxEventBehavior {
        /**
         * Creates a drop behavior.
         */
        public DropBehavior() {
            super("drop");
        }

        /**
         * Renders to the web response what the component wants to contribute.
         *
         * @param response response object
         */
        @Override
        public void renderHead(Component component, IHeaderResponse response) {
            super.renderHead(component, response);
            String javaScript = "jQuery(document).on('drop', '#" + getMarkupId() + "', function (event) { " +
                    "event.preventDefault(); });";
            response.render(OnDomReadyHeaderItem.forScript(javaScript));
        }

        /**
         * Called when a draggable is dropped on a node.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        protected void onEvent(AjaxRequestTarget target) {
            AbstractNestedTree<T> tree = getTree();
            Request request = getComponent().getRequest();
            StringValue data = request.getRequestParameters().getParameterValue("data");

            if (data.toString() != null && tree instanceof IDraggableTree) {
                String relativePath = data.toString();
                IDraggableTree draggableTree = (IDraggableTree) tree;
                Component sourceComponent = getPage().get(relativePath);
                Component targetComponent = getComponent();

                if (sourceComponent != null) {
                    for (IDropListener dropListener : draggableTree.getDropListeners()) {
                        dropListener.onDrop(sourceComponent, targetComponent);
                    }
                }
            }
        }

        /**
         * Modifies Ajax request attributes.
         *
         * @param attributes Ajax request attributes
         */
        @Override
        protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
            super.updateAjaxAttributes(attributes);
            String javaScript = "var dataTransfer = attrs.event.originalEvent.dataTransfer; " +
                    "return { data: (!dataTransfer ? '' : dataTransfer.getData('text/plain')) };";
            attributes.getDynamicExtraParameters().add(javaScript);
        }
    }
}
