package lemming.sense;

import lemming.ui.tree.AbstractNestedTree;
import lemming.ui.tree.INestedTreeProvider;
import lemming.ui.tree.NodeLabel;
import org.apache.wicket.Component;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A nested tree for senses.
 */
public class SenseTree extends AbstractNestedTree<Sense> {
    /**
     * Creates a sennse tree,
     *
     * @param id ID of a tree
     * @param provider provider of tree data
     */
    public SenseTree(String id, INestedTreeProvider<Sense> provider) {
        super(id, provider);
    }

    /**
     * Creates a sennse tree,
     *
     * @param id ID of a tree
     * @param provider provider of tree data
     * @param selectedSenseObject model of the selected sense object
     */
    public SenseTree(String id, INestedTreeProvider<Sense> provider, Sense selectedSenseObject) {
        super(id, provider);
    }

    /**
     * Creates a new content component.
     *
     * @param id ID of the component
     * @param model model of the node object
     * @return A node label component.
     */
    @Override
    protected Component newContentComponent(String id, IModel<Sense> model) {
        return new NodeLabel<Sense>(id) {
            @Override
            protected String getLabelText() {
                return model.getObject().getMeaning();
            }
        };
    }

    /**
     * Defines which sense nodes are expanded.
     *
     * @param selectedNode the selected node object
     * @return A model with a set of senses.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected IModel<Set<Sense>> createExpandState(Sense selectedNode) {
        Iterator iterator = getProvider().getRoots();
        HashSet<Sense> expandedSenses = new HashSet<Sense>();

        while (iterator.hasNext()) {
            expandedSenses.add((Sense) iterator.next());
        }

        return new Model(expandedSenses);
    }

    /**
     * Renders to the web response what the component wants to contribute.
     *
     * @param response response object
     */
    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        ResourceReference styleReference = new CssResourceReference(SenseTree.class, "styles/sense-tree.css");
        response.render(CssHeaderItem.forReference(styleReference));
    }
}
