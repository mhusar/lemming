package lemming.context.inbound;

import lemming.context.BaseContext;
import lemming.context.Context;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

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
    private MultivaluedMap<Context, InboundContext> map;

    /**
     * Creates a tree provider for contexts.
     *
     * @param matchingTriples matching triples
     */
    public ContextTreeProvider(List<Triple> matchingTriples, List<Context> contextsWithoutComplement) {
        this.contexts = new ArrayList<>();
        map = new MultivaluedHashMap<>();
        applyTriples(matchingTriples);
        applyContexts(contextsWithoutComplement);
    }

    /**
     * Applies contexts of triples to a map of contexts.
     *
     * @param triples list of triples
     */
    private void applyTriples(List<Triple> triples) {
        for (Triple triple : triples) {
            contexts.add(triple.getContext());
            map.putSingle(triple.getContext(), triple.getInboundContext());
        }

        contexts.sort(new ContextComparator());
    }

    /**
     * Applies contexts to list of contexts.
     *
     * @param contexts list of contexts
     */
    private void applyContexts(List<Context> contexts) {
        this.contexts.addAll(contexts);
        this.contexts.sort(new ContextComparator());
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
            children.addAll(map.get(context));
        }

        children.sort(new ContextComparator());
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
        InboundContextDao inboundContextDao = new InboundContextDao();
        inboundContext = inboundContextDao.refresh(inboundContext);
        inboundContext.setMatch(context);
        inboundContext = inboundContextDao.merge(inboundContext);

        if (hasChildren(context)) {
            map.add(context, inboundContext);
        } else {
            map.putSingle(context, inboundContext);
        }
    }

    /**
     * Removes an inbound context from a context.
     *
     * @param context a context
     */
    public void remove(Context context, InboundContext inboundContext) {
        InboundContextDao inboundContextDao = new InboundContextDao();
        inboundContext = inboundContextDao.refresh(inboundContext);

        map.remove(context, inboundContext);
        inboundContext.setMatch(null);
        inboundContextDao.merge(inboundContext);
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
