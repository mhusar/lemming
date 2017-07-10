package lemming.sense;

import lemming.lemma.Lemma;
import lemming.tree.INestedTreeProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Iterator;

/**
 * A tree provider for senses.
 */
public class SenseTreeProvider implements INestedTreeProvider<Sense> {
    /**
     * Lemma of senses.
     */
    private Lemma lemma;

    /**
     * Creates a sense tree provider.
     *
     * @param lemma lemma of sense
     */
    public SenseTreeProvider(Lemma lemma) {
        this.lemma = lemma;
    }

    /**
     * Returns the roots of a tree.
     *
     * @return An iterator with senses.
     */
    @Override
    public Iterator<? extends Sense> getRoots() {
        return new SenseDao().findRootNodes(lemma).iterator();
    }

    /**
     * Checks if a node has children.
     *
     * @param node sense node
     * @return True if a sense has children, false otherwise.
     */
    @Override
    public boolean hasChildren(Sense node) {
        if (new SenseDao().isTransient(node)) {
            return false;
        }

        Sense refreshedNode = new SenseDao().refresh(node);
        return !refreshedNode.getChildren().isEmpty();
    }

    /**
     * Returns children of a tree.
     *
     * @param node sense node
     * @return An iterator with senses.
     */
    @Override
    public Iterator<? extends Sense> getChildren(Sense node) {
        return new SenseDao().getChildren(node).iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasParent(Sense node) {
        return new SenseDao().getParent(node) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sense getParent(Sense node) {
        return new SenseDao().getParent(node);
    }

    /**
     * Wraps a sense object into a model.
     *
     * @param object sense object
     * @return A model.
     */
    @Override
    public IModel<Sense> model(Sense object) {
        return new Model<>(object);
    }

    /**
     * Does nothing.
     */
    @Override
    public void detach() {
    }
}
