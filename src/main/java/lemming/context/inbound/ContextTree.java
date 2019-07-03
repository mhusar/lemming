package lemming.context.inbound;

import lemming.context.BaseContext;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.NestedTree;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;

import java.util.Iterator;

/**
 * A tree used to map inbound contexts to contexts.
 */
public class ContextTree extends NestedTree<BaseContext> {

    /**
     * A data view for inbound contexts.
     */
    private DataView<InboundContext> dataView;

    /**
     * The currently selected context.
     */
    private BaseContext selectedContext;

    /**
     * Creates a tree.
     *
     * @param provider tree provider
     */
    public ContextTree(ITreeProvider<BaseContext> provider) {
        super("inboundContextTree", provider);
    }

    /**
     * Called on initialize.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void onInitialize() {
        super.onInitialize();
        dataView = (DataView<InboundContext>) getParent().get("listContainer:inboundContextDataView");
    }

    /**
     * Creates a content component.
     *
     * @param id if of the component
     * @param model model of the component
     * @return The component.
     */
    @Override
    protected Component newContentComponent(String id, IModel<BaseContext> model) {
        return new ContextFolder(id, this, dataView, model).setOutputMarkupId(true);
    }

    /**
     * Collapses all roots.
     */
    public void collapseAll() {
        Iterator<? extends BaseContext> iterator = getProvider().getRoots();

        while (iterator.hasNext()) {
            collapse(iterator.next());
        }
    }

    /**
     * Expands all roots.
     */
    public void expandAll() {
        Iterator<? extends BaseContext> iterator = getProvider().getRoots();

        while (iterator.hasNext()) {
            expand(iterator.next());
        }
    }

    /**
     * Sets a context as selected context.
     *
     * @param context a context
     */
    public void setSelectedContext(BaseContext context) {
        selectedContext = context;
    }

    /**
     * Returns the selected context.
     *
     * @return The selected context or null.
     */
    public BaseContext getSelectedContext() {
        return selectedContext;
    }
}
