package lemming.sense;

import lemming.ui.tree.GenericFolder;
import lemming.ui.tree.GenericNestedTree;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * Folder representation of a sense node.
 */
public class SenseFolder extends GenericFolder<Sense> {
    /**
     * Creates a sense folder.
     *
     * @param id ID of a folder
     * @param tree tree the folder is be attached to
     * @param model model of node data
     */
    public SenseFolder(String id, GenericNestedTree<Sense> tree, IModel<Sense> model) {
        super(id, tree, model);
    }

    /**
     * Creates a model for the label.
     *
     * @param model sense model
     * @return A property model for a sense’s meaning.
     */
    @Override
    protected IModel<String> newLabelModel(IModel<Sense> model) {
        return new PropertyModel<String>(model, "meaning");
    }
}
