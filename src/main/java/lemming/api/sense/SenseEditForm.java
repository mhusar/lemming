package lemming.api.sense;

import org.apache.wicket.Page;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

/**
 * A form for editing senses.
 */
public class SenseEditForm extends Form<SenseWrapper> {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Class of the next page.
     */
    private Class<? extends Page> nextPageClass;

    /**
     * Creates a new sense edit form.
     * 
     * @param id
     *            ID of the edit form
     * @param model
     *            sense wrapper model that is edited
     * @param nextPageClass
     *            class of the next page
     */
    public SenseEditForm(String id, IModel<SenseWrapper> model, Class<? extends Page> nextPageClass) {
        super(id, model);
        this.nextPageClass = nextPageClass;
        Sense selectedSense = model.getObject().getSense();
        ITreeProvider<Sense> treeProvider = new SenseTreeProvider(model.getObject().getLemma());
        AbstractTree<Sense> senseTree = new SenseTree("senses", treeProvider, selectedSense);

        add(senseTree);
    }
}
