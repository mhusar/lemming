package lemming.tree;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import java.util.HashSet;
import java.util.Set;

/**
 * A tree implementation with node selection.
 *
 * @param <T> data type
 */
public abstract class AbstractNestedTree<T> extends AbstractTree<T> {
    /**
     * Model of the selected node object.
     */
    private IModel<T> selectedObjectModel;

    /**
     * Listener for select events.
     */
    private Set<ISelectListener<T>> selectListeners = new HashSet<ISelectListener<T>>();

    /**
     * Creates a nested tree.
     *
     * @param id ID of the tree
     * @param provider tree provider for nested trees
     */
    public AbstractNestedTree(String id, INestedTreeProvider<T> provider) {
        this(id, provider, null);
    }

    /**
     * Creates a nested tree.
     *
     * @param id ID of the tree
     * @param provider tree provider for nested trees
     * @param selectedNode the selected node object which may be null
     */
    public AbstractNestedTree(String id, INestedTreeProvider<T> provider, T selectedNode) {
        super(id, provider);
        setModel(createExpandState(selectedNode));
        selectedObjectModel = provider.model(selectedNode);

        if (getModel() == null || getModel().getObject() == null) {
            throw new IllegalStateException("The state model or its object are not allowed to be null!");
        }

        if (selectedObjectModel == null) {
            throw new IllegalStateException("The selected model is not allowed to be null!");
        }

        add(new Subtree<T>("subtree", this, null));
        setOutputMarkupId(true);
    }

    /**
     * Defines which nodes are expanded.
     *
     * @param selectedNode the selected node object
     * @return A set of expanded node objects.
     */
    protected abstract IModel<Set<T>> createExpandState(T selectedNode);

    /**
     * Adds a listener for select events.
     *
     * @param listener a listener object
     */
    public void addSelectListener(ISelectListener<T> listener) {
        selectListeners.add(listener);
    }

    /**
     * Returns the model of the selected node object.
     *
     * @return A model of the selected node object.
     */
    public IModel<T> getSelectedObjectModel() {
        return selectedObjectModel;
    }

    /**
     * Sets a given object as selected.
     *
     * @param target target that produces an Ajax response
     * @param object a given object
     */
    public void select(AjaxRequestTarget target, T object) {
        T selectedObject = selectedObjectModel.getObject();

        modelChanging();
        selectedObjectModel.setObject(object);
        modelChanged();
        updateBranch(selectedObject, target);
        updateBranch(object, target);

        for (ISelectListener<T> selectListener : selectListeners) {
            selectListener.onSelect(target, object);
        }
    }

    /**
     * Deselects the selected object.
     *
     * @param target target that produces an Ajax response
     */
    public void deselect(AjaxRequestTarget target) {
        T selectedObject = selectedObjectModel.getObject();

        modelChanging();
        selectedObjectModel.setObject(null);
        modelChanged();
        updateNode(selectedObject, target);

        for (ISelectListener<T> selectListener : selectListeners) {
            selectListener.onDeselect(target);
        }
    }

    /**
     * Expands the given node. Updates the affected branch.
     *
     * @param target target that produces an Ajax response
     * @param object node object to expand
     */
    public void expand(AjaxRequestTarget target, T object) {
        super.expand(object);
    }

    /**
     * Collapses the given node. Updates the affected branch.
     *
     * @param target target that produces an Ajax response
     * @param object node object to collapse
     */
    public void collapse(AjaxRequestTarget target, T object) {
        super.collapse(object);
        T selectedObject = selectedObjectModel.getObject();
        T selectedObjectAncestor = selectedObject;

        if (selectedObjectAncestor != null) {
            while (getProvider().hasParent(selectedObjectAncestor)) {
                T parentObject = getProvider().getParent(selectedObjectAncestor);

                if (parentObject.equals(object)) {
                    deselect(target);
                    select(target, object);
                    break;
                }

                selectedObjectAncestor = parentObject;
            }
        }
    }

    /**
     * Returns the provider for the tree.
     *
     * @return A tree provider.
     */
    @Override
    public INestedTreeProvider<T> getProvider() {
        return (INestedTreeProvider<T>) super.getProvider();
    }

    /**
     * Creates a node component.
     *
     * @param id ID of the component
     * @param model model of the node object
     * @return A node component.
     */
    @Override
    public abstract Component newNodeComponent(String id, IModel<T> model);

    /**
     * Creates a new content component.
     *
     * @param id ID of the component
     * @param model model of the node object
     * @return A content component.
     */
    @Override
    protected abstract Component newContentComponent(String id, IModel<T> model);

    /**
     * Processes the component tag.
     *
     * @param tag tag to modify
     */
    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        tag.put("class", "tree");
    }

    /**
     * Updates the node of a node object.
     *
     * @param object a node object
     * @param handler request handler that allows partial updates of the current page instance
     */
    @Override
    public void updateNode(T object, final IPartialPageRequestHandler handler) {
        if (handler != null) {
            final IModel<T> model = getProvider().model(object);

            visitChildren(Node.class, new IVisitor<Node<T>, Void>() {
                @Override
                public void component(Node<T> node, IVisit<Void> visit) {
                    if (model.equals(node.getModel())) {
                        handler.add(node);
                        visit.stop();
                    }

                    visit.dontGoDeeper();
                }
            });
            model.detach();
        }
    }

    /**
     * Updates the branch of a node object.
     *
     * @param object a node object
     * @param handler request handler that allows partial updates of the current page instance
     */
    @Override
    public void updateBranch(T object, final IPartialPageRequestHandler handler) {
        if (handler != null) {
            final IModel<T> model = getProvider().model(object);

            visitChildren(Branch.class, new IVisitor<Branch<T>, Void>() {
                @Override
                public void component(Branch<T> branch, IVisit<Void> visit) {
                    if (model.equals(branch.getModel())) {
                        handler.add(branch);
                        visit.stop();
                    }
                }
            });
            model.detach();
        }
    }

    /**
     * Interface for a select listener.
     */
    public interface ISelectListener<T> {
        /**
         * Called when a select event occurs.
         *
         * @param target target that produces an Ajax response
         * @param object the selected object
         */
        void onSelect(AjaxRequestTarget target, T object);

        /**
         * Called when a deselect event occurs.
         *
         * @param target target that produces an Ajax response
         */
        void onDeselect(AjaxRequestTarget target);
    }
}
