package lemming.context.inbound;

import lemming.context.BaseContext;
import lemming.context.Context;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
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
     * @return A merged inbound context.
     */
    public InboundContext add(Context context, InboundContext inboundContext) {
        InboundContextDao inboundContextDao = new InboundContextDao();
        inboundContext = inboundContextDao.refresh(inboundContext);
        inboundContext = inboundContextDao.merge(inboundContext);

        map.add(context, inboundContext);
        return inboundContext;
    }

    /**
     * Removes an inbound context from a context.
     *
     * @param inboundContext an inbound context
     * @return A merged inbound context.
     */
    public InboundContext remove(InboundContext inboundContext) {
        InboundContextDao inboundContextDao = new InboundContextDao();

        for (Map.Entry<Context, List<InboundContext>> entry : map.entrySet()) {
            Context context = entry.getKey();
            List<InboundContext> inboundContexts = entry.getValue();

            if (inboundContexts.contains(inboundContext)) {
                inboundContexts.remove(inboundContext);

                if (inboundContexts.isEmpty()) {
                    map.remove(context);
                }

                break;
            }
        }

        inboundContext = inboundContextDao.refresh(inboundContext);
        inboundContext.setMatch(null);
        return inboundContextDao.merge(inboundContext);
    }

    /**
     * A comparator for contexts.
     */
    private class ContextComparator implements Comparator<BaseContext> {
        /**
         * Compares two contexts.
         *
         * @param context1 context 1
         * @param context2 context 2
         * @return A negative or a positive number.
         */
        @Override
        public int compare(BaseContext context1, BaseContext context2) {
            if (context1.getNumber() < context2.getNumber()) {
                return -1;
            } else if (context1.getNumber() > context2.getNumber()) {
                return 1;
            } else {
                throw new IllegalArgumentException();
            }
        }
    }
}
