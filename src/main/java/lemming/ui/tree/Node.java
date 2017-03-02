package lemming.ui.tree;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * A selectable tree node.
 *
 * @param <T> data type
 */
public class Node<T> extends Panel {
    /**
     * The owning tree.
     */
    private AbstractNestedTree<T> tree;

    /**
     * Creates a tree node.
     *
     * @param id ID of the node
     * @param tree the owning tree
     * @param model model of the node object
     */
    public Node(String id, AbstractNestedTree<T> tree, IModel<T> model) {
        super(id, model);
        this.tree = tree;

        add(new NodeSwitch("switch"));
        add(tree.newContentComponent("content", model));
        add(new SelectBehavior());
        add(new StyleBehavior());
        setOutputMarkupId(true);
    }

    /**
     * Returns the model of a node.
     *
     * @return A model.
     */
    @SuppressWarnings("unchecked")
    public IModel<T> getModel() {
        return (IModel<T>) getDefaultModel();
    }

    /**
     * Returns the model object of a node.
     *
     * @return A model object.
     */
    @SuppressWarnings("unchecked")
    public T getModelObject() {
        return (T) getDefaultModelObject();
    }

    /**
     * A toggle switch to expand/collapse nodes.
     */
    private class NodeSwitch extends AjaxLink<Void> {
        /**
         * Create a node switch.
         *
         * @param id ID of the switch
         */
        public NodeSwitch(String id) {
            super(id);
        }

        /**
         * Called when a node switch is clicked.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            T modelObject = Node.this.getModelObject();

            if (tree.getState(modelObject) == AbstractTree.State.EXPANDED) {
                tree.collapse(modelObject);
            } else {
                tree.expand(modelObject);
            }
        }

        /**
         * Processes the component tag.
         *
         * @param tag tag to modify
         */
        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);
            tag.put("class", "node-switch");
        }

        /**
         * Returns wether a switch is enabled.
         *
         * @return True if the model object has children.
         */
        @Override
        public boolean isEnabled() {
            return tree.getProvider().hasChildren(Node.this.getModelObject());
        }

        /**
         * Modifies Ajax request attributes.
         *
         * @param attributes Ajax request attributes
         */
        @Override
        protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
            super.updateAjaxAttributes(attributes);
            attributes.setEventPropagation(AjaxRequestAttributes.EventPropagation.STOP);
        }
    }

    /**
     * A behavior which makes a tree nodes selectable.
     */
    private class SelectBehavior extends AjaxEventBehavior {
        public SelectBehavior() {
            super("click");
        }

        /**
         * Called when a node is clicked.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        protected void onEvent(AjaxRequestTarget target) {
            tree.select(target, getModelObject());
        }
    }

    /**
     * A behavior that adds style to a node.
     */
    private class StyleBehavior extends Behavior {
        /**
         * Processes the component tag.
         *
         * @param component component that renders the component tag
         * @param tag tag to modify
         */
        @Override
        @SuppressWarnings("unchecked")
        public void onComponentTag(Component component, ComponentTag tag) {
            super.onComponentTag(component, tag);
            Node<T> node = (Node<T>) component;
            String styleClass = getStyleClass(node);

            tag.put("class", "node " + styleClass);
        }

        /**
         * Returns the style class of a node.
         *
         * @param node a tree node
         * @return A style class as string.
         */
        private String getStyleClass(Node<T> node) {
            T modelObject = node.getModelObject();
            String styleClass;

            if (tree.getProvider().hasChildren(modelObject)) {
                if (tree.getState(modelObject) == AbstractTree.State.EXPANDED) {
                    styleClass = "node-expanded";
                } else {
                    styleClass = "node-collapsed";
                }
            } else {
                styleClass = "node-other";
            }

            if (tree.getSelectedObjectModel().equals(node.getModel())) {
                styleClass += " node-selected";
            }

            return styleClass;
        }
    }
}
