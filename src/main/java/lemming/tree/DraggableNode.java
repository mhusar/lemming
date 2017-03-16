package lemming.tree;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Request;
import org.apache.wicket.util.string.StringValue;

/**
 * A selectable tree node.
 *
 * To enable behaviors of draggable nodes external JavaScript code must be enabled.
 *
 * Add this code to method renderHead() of the parent tree:
 * ResourceReference reference = new JavaScriptResourceReference(DraggableNode.class, "scripts/draggable-node.js");
 * response.render(JavaScriptHeaderItem.forReference(reference));
 *
 * @param <T> data type
 */
public class DraggableNode<T> extends BaseNode<T> {
    /**
     * Creates a tree node.
     *
     * @param id ID of the node
     * @param tree the owning tree
     * @param model model of the node object
     */
    public DraggableNode(String id, AbstractNestedTree<T> tree, IModel<T> model) {
        super(id, tree, model);
        Dropzone bottomDropzone = new Dropzone("bottomDropzone", DropzoneType.BOTTOM);
        Dropzone middleDropzone = new Dropzone("middleDropzone", DropzoneType.MIDDLE);
        Dropzone topDropzone = new Dropzone("topDropzone", DropzoneType.TOP);

        bottomDropzone.add(new DragenterBehavior());
        bottomDropzone.add(new DragoverBehavior());
        bottomDropzone.add(new DragleaveBehavior());
        bottomDropzone.add(new DropBehavior());
        add(bottomDropzone);

        middleDropzone.add(new NodeSwitch("switch"));
        middleDropzone.add(getTree().newContentComponent("content", getModel()));
        middleDropzone.add(new DragenterBehavior());
        middleDropzone.add(new DragoverBehavior());
        middleDropzone.add(new DragleaveBehavior());
        middleDropzone.add(new DragstartBehavior());
        middleDropzone.add(new DragendBehavior());
        middleDropzone.add(new DropBehavior());
        add(middleDropzone);

        topDropzone.add(new DragenterBehavior());
        topDropzone.add(new DragoverBehavior());
        topDropzone.add(new DragleaveBehavior());
        topDropzone.add(new DropBehavior());
        add(topDropzone);

        add(new SelectBehavior());
        add(new StyleBehavior());
        setOutputMarkupId(true);
    }

    /**
     * Dropzone type.
     */
    private enum DropzoneType {
        BOTTOM, MIDDLE, TOP
    }

    /**
     * Additional dropzones for draggable nodes.
     */
    private class Dropzone extends WebMarkupContainer {
        /**
         * Dropzone type.
         */
        private DropzoneType type;

        /**
         * Creates a dropzone
         *
         * @param id ID of the dropzone
         * @param type dropzone type
         */
        public Dropzone(String id, DropzoneType type) {
            super(id);
            this.type = type;
            setOutputMarkupId(true);
        }

        /**
         * Returns the dropzone type.
         *
         * @return A dropzone type.
         */
        public DropzoneType getType() {
            return type;
        }

        /**
         * Processes the component tag.
         *
         * @param tag tag to modify
         */
        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);

            if (type.equals(DropzoneType.BOTTOM)) {
                tag.put("class", "dropzone dropzone-bottom");
            } else if (type.equals(DropzoneType.MIDDLE)) {
                tag.put("class", "dropzone dropzone-middle");
                tag.put("draggable", "true");
            } else if (type.equals(DropzoneType.TOP)) {
                tag.put("class", "dropzone dropzone-top");
            }
        }
    }

    /**
     * A behavior which fires on drag enter.
     */
    private class DragenterBehavior extends Behavior {
        /**
         * Renders to the web response what the component wants to contribute.
         *
         * @param response response object
         */
        @Override
        public void renderHead(Component component, IHeaderResponse response) {
            super.renderHead(component, response);
            String dropzoneId = component.getMarkupId(), nodeId = component.getParent().getMarkupId();
            String javaScript = "DraggableNode.onDragenter('" + dropzoneId + "', '" + nodeId + "');";
            response.render(OnDomReadyHeaderItem.forScript(javaScript));
        }
    }

    /**
     * A behavior which fires on drag over.
     */
    private class DragoverBehavior extends Behavior {
        /**
         * Renders to the web response what the component wants to contribute.
         *
         * @param response response object
         */
        @Override
        public void renderHead(Component component, IHeaderResponse response) {
            super.renderHead(component, response);
            String dropzoneId = component.getMarkupId();
            String javaScript = "DraggableNode.onDragover('" + dropzoneId + "');";
            response.render(OnDomReadyHeaderItem.forScript(javaScript));
        }
    }

    /**
     * A behavior which fires on drag leave.
     */
    private class DragleaveBehavior extends Behavior {
        /**
         * Renders to the web response what the component wants to contribute.
         *
         * @param response response object
         */
        @Override
        public void renderHead(Component component, IHeaderResponse response) {
            super.renderHead(component, response);
            String dropzoneId = component.getMarkupId(), nodeId = component.getParent().getMarkupId();
            String javaScript = "DraggableNode.onDragleave('" + dropzoneId + "', '" + nodeId + "');";
            response.render(OnDomReadyHeaderItem.forScript(javaScript));
        }
    }

    /**
     * A behavior which fires on drag start.
     */
    private class DragstartBehavior extends Behavior {
        /**
         * Renders to the web response what the component wants to contribute.
         *
         * @param response response object
         */
        @Override
        public void renderHead(Component component, IHeaderResponse response) {
            super.renderHead(component, response);
            String dropzoneId = component.getMarkupId(), nodeId = component.getParent().getMarkupId(),
                    treeId = getTree().getMarkupId(), nodeRelativePath = component.getParent().getPageRelativePath();
            String javaScript = "DraggableNode.onDragstart('" + dropzoneId + "', '" + nodeId + "', '" + treeId +
                    "', '" + nodeRelativePath + "');";
            response.render(OnDomReadyHeaderItem.forScript(javaScript));
        }
    }

    /**
     * A behavior which fires on drag end.
     */
    private class DragendBehavior extends Behavior {
        /**
         * Renders to the web response what the component wants to contribute.
         *
         * @param response response object
         */
        @Override
        public void renderHead(Component component, IHeaderResponse response) {
            super.renderHead(component, response);
            String dropzoneId = component.getMarkupId(), nodeId = component.getParent().getMarkupId(),
                    treeId = getTree().getMarkupId();
            String javaScript = "DraggableNode.onDragend('" + dropzoneId + "', '" + nodeId + "', '" + treeId + "');";
            response.render(OnDomReadyHeaderItem.forScript(javaScript));
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
            String dropzoneId = component.getMarkupId();
            String javaScript = "DraggableNode.onDrop('" + dropzoneId + "');";
            response.render(OnDomReadyHeaderItem.forScript(javaScript));
        }

        /**
         * Called when a draggable is dropped on a node.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        @SuppressWarnings("unchecked")
        protected void onEvent(AjaxRequestTarget target) {
            AbstractNestedTree<T> tree = getTree();
            Request request = getComponent().getRequest();
            StringValue data = request.getRequestParameters().getParameterValue("data");

            if (data.toString() != null && tree instanceof IDraggableTree) {
                String relativePath = data.toString();
                Dropzone dropzone = (Dropzone) getComponent();
                Component sourceComponent = getPage().get(relativePath);
                Component targetComponent = dropzone.getParent();

                if (targetComponent.equals(sourceComponent)) {
                    return;
                }

                for (IDropListener dropListener : ((IDraggableTree) tree).getDropListeners()) {
                    if (dropzone.getType().equals(DropzoneType.BOTTOM)) {
                        dropListener.onBottomDrop(target, sourceComponent, targetComponent);
                    } else if (dropzone.getType().equals(DropzoneType.MIDDLE)) {
                        dropListener.onMiddleDrop(target, sourceComponent, targetComponent);
                    } else if (dropzone.getType().equals(DropzoneType.TOP)) {
                        dropListener.onTopDrop(target, sourceComponent, targetComponent);
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
