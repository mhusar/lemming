package lemming.api.sense;

import lemming.api.ui.tree.GenericNestedTree;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.model.IModel;

/**
 * A nested tree for senses.
 */
public class SenseTree extends GenericNestedTree<Sense> {
    /**
     * Creates a sennse tree,
     *
     * @param id ID of a tree
     * @param provider provider of tree data
     */
    public SenseTree(String id, ITreeProvider<Sense> provider) {
        super(id, provider);
    }

    /**
     * Creates a sennse tree,
     *
     * @param id ID of a tree
     * @param provider provider of tree data
     * @param selectedSense selected sense
     */
    public SenseTree(String id, ITreeProvider<Sense> provider, Sense selectedSense) {
        super(id, provider);
        select(selectedSense);
    }

    /**
     * Creates a new component for the content of a node.
     *
     * @param id ID of the component
     * @param model model containing the node data
     * @return A folder component.
     */
    @Override
    protected Component newContentComponent(String id, IModel<Sense> model) {
        Folder<Sense> folder = new SenseFolder(id, this, model);
        folder.setOutputMarkupId(true);
        return folder;
    }
}