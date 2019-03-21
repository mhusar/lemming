package lemming.context.inbound;

import lemming.context.BaseContext;
import lemming.context.Context;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.*;

/**
 * A tree provider for contexts.
 */
public class ContextTreeProvider implements ITreeProvider<BaseContext> {
    /**
     * List of contexts.
     */
    private List<Context> contexts;

    /**
     * A map of contexts.
     */
    private Map<Context, InboundContext> map;

    /**
     * Creates a tree provider for contexts.
     *
     * @param matchingTriples matching triples
     */
    public ContextTreeProvider(List<Triple> matchingTriples) {
        this.contexts = new ArrayList<>();
        map = new HashMap<>();
        applyTriples(matchingTriples);
    }

    /**
     * Applies contexts of triples to a map of contexts.
     *
     * @param triples list of triples
     */
    private void applyTriples(List<Triple> triples) {
        for (Triple triple : triples) {
            contexts.add(triple.getContext());
            map.put(triple.getContext(), triple.getInboundContext());
        }

        contexts.sort(new ContextComparator());
    }

    /**
     * Returns the roots of the tree.
     *
     * @return An iterator for the roots of a tree.
     */
    @Override
    public Iterator<? extends BaseContext> getRoots() {
        return contexts.iterator();
    }

    /**
     * Checks if the given context has children.
     *
     * @param context a context
     * @return True if a triple for the given context exists.
     */
    @Override
    public boolean hasChildren(BaseContext context) {
        if (context instanceof Context) {
            return map.containsKey(context);
        }

        return false;
    }

    /**
     * Returns an iterator for the children of a context.
     *
     * @param context a context
     * @return An iterator for the children of a context.
     */
    @Override
    public Iterator<? extends BaseContext> getChildren(BaseContext context) {
        List<BaseContext> children = new ArrayList<>();

        if (context instanceof Context && hasChildren(context)) {
            children.add(map.get(context));
        }

        return children.iterator();
    }

    /**
     * Wraps objects inside a model.
     *
     * @param context a context object.
     * @return A context model.
     */
    @Override
    public IModel<BaseContext> model(BaseContext context) {
        return Model.of(context);
    }

    /**
     * Detaches model after use.
     */
    @Override
    public void detach() {
        // nothing to do
    }

    /**
     * Adds an inbound context to a context.
     *
     * @param context a context
     * @param inboundContext an inbound context
     */
    public void add(Context context, InboundContext inboundContext) {
        contexts.add(context);
        contexts.sort(new ContextComparator());
        map.put(context, inboundContext);
    }

    /**
     * Removes an inbound context from a context.
     *
     * @param context a context
     * @return The removed inbound context.
     */
    public InboundContext remove(Context context) {
        contexts.remove(context);
        return map.remove(context);
    }

    /**
     * A comparator for contexts.
     */
    private class ContextComparator implements Comparator<BaseContext> {
        @Override
        public int compare(BaseContext b1, BaseContext b2) {
            if (b1.getNumber() < b2.getNumber()) {
                return -1;
            } else if (b1.getNumber() > b2.getNumber()) {
                return 1;
            } else {
                throw new IllegalArgumentException();
            }
        }
    }
}
