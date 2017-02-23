package lemming.ui.tree;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.NestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import java.util.Iterator;

/**
 * A nested tree with select functionality and custom style.
 *
 * @param <T> data type displayed
 */
public class GenericNestedTree<T> extends NestedTree<T> {
    /**
     * Model of the selected node.
     */
    private IModel<T> selectedNodeModel;

    /**
     * Listener for select events.
     */
    private SelectListener listener;

    /**
     * Interface for a select listener.
     */
    public interface SelectListener {
        /**
         * Called when a select event occurs.
         *
         * @param target target that produces an Ajax response
         */
        void onSelect(AjaxRequestTarget target);
    }

    /**
     * Creates a nested tree.
     *
     * @param id ID of a tree
     * @param provider provider of tree data
     */
    public GenericNestedTree(String id, ITreeProvider<T> provider) {
        super(id, provider);
        add(new StyleBehavior());
        selectedNodeModel = new Model();
    }

    /**
     * Creates a nested tree.
     *
     * @param id ID of a tree
     * @param provider provider of tree data
     * @param selectedNodeModel model of the selected node object
     */
    public GenericNestedTree(String id, ITreeProvider<T> provider, IModel<T> selectedNodeModel) {
        super(id, provider);
        add(new StyleBehavior());
        this.selectedNodeModel = selectedNodeModel;
    }

    /**
     * Creates a component for the content of a node.
     *
     * @param id ID of the component
     * @param model model containing the node data
     * @return A folder component.
     */
    @Override
    protected Component newContentComponent(String id, IModel<T> model) {
        Folder<T> folder = new GenericFolder(id, this, model);
        folder.setOutputMarkupId(true);
        return folder;
    }

    /**
     * Expands all tree branches.
     */
    public void expandAll() {
        Iterator<? extends T> rootsIterator = getProvider().getRoots();

        while (rootsIterator.hasNext()) {
            expand(rootsIterator.next());
        }
    }

    /**
     * Selects the given node.
     *
     * @param target target that produces an Ajax response
     * @param object object to select
     */
    public void select(AjaxRequestTarget target, T object) {
        modelChanging();
        getModelObject().clear();
        getModelObject().add(object);
        selectedNodeModel.setObject(object);
        modelChanged();

        if (listener instanceof GenericNestedTree.SelectListener) {
            listener.onSelect(target);
        }

        updateNode(object, target);
        expandAll();
        target.add(this);
    }

    /**
     * Returns the model of the selected node.
     *
     * @return A model of the selected node or an empty model if no node is selected.
     */
    public IModel<T> getSelectedNodeModel() {
        return selectedNodeModel;
    }

    /**
     * Registers a select listener.
     *
     * @param listener select listener
     */
    public void registerSelectListener(SelectListener listener) {
        this.listener = listener;
    }

    /**
     * Custom style behavior.
     */
    private class StyleBehavior extends Behavior {
        /**
         * Renders to the web response what the component wants to contribute.
         *
         * @param component component object
         * @param response response object
         */
        @Override
        public void renderHead(Component component, IHeaderResponse response) {
            ResourceReference styleReference = new CssResourceReference(GenericNestedTree.class, "styles/style.css");
            response.render(CssHeaderItem.forReference(styleReference));
        }
    }
}
