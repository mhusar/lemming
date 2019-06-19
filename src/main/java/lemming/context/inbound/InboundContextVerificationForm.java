package lemming.context.inbound;

import lemming.context.Context;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;

import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;

/**
 * A form for the verification of inbound contexts.
 */
class InboundContextVerificationForm extends Form<InboundContextPackage> {

    RepeatingView repeatingView;

    /**
     * Creates a context verify form.
     *
     * @param model model of a group of inbound contexts
     */
    public InboundContextVerificationForm(IModel<InboundContextPackage> model) {
        super("InboundContextVerificationForm", model);

        repeatingView = new RepeatingView("repeater");
        add(repeatingView);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        InboundContextPackage contextPackage = getModelObject();

        List<String> unmatchedLocations = findUnmatchedLocations(contextPackage);

        for (String location : unmatchedLocations) {
            List<InboundContext> unmatchedContexts = new InboundContextPackageDao()
                    .findUnmatchedContextsByLocation(contextPackage, location);
            List<Context> possibleComplements = new InboundContextDao().findPossibleComplements(unmatchedContexts);
            List<Triple> matchingTriples = computeMatchingTriples(unmatchedContexts, possibleComplements);
            List<InboundContext> contextsWithoutComplement = getContextsWithoutComplement(unmatchedContexts,
                    matchingTriples);
            List<Context> unmatchedComplements = getUnmatchedComplements(possibleComplements, matchingTriples);
            ContextTreeProvider provider = new ContextTreeProvider(matchingTriples, unmatchedComplements);

            repeatingView.add(new ContextTreePanel(repeatingView.newChildId(), location, provider,
                    contextsWithoutComplement));
        }

        // TODO: show something else
        if (repeatingView.size() == 0) {
            repeatingView.setVisible(false);
        }
    }

    /**
     * Finds and sets locations with unmatched contexts.
     *
     * @param contextPackage package of inbound contexts
     */
    private List<String> findUnmatchedLocations(InboundContextPackage contextPackage) {
        return new InboundContextPackageDao().findUnmatchedContextLocations(contextPackage);
    }

    /**
     * Computes matching triples for a list of contexts.
     *
     * @param contexts a list of inbound contexts
     * @param complements a list of context complements
     * @return A list of matching triples.
     */
    private List<Triple> computeMatchingTriples(List<InboundContext> contexts, List<Context> complements) {
        if (complements != null) {
            MultivaluedMap<Integer, Triple> distanceMap = MatchHelper.getTriples(complements, contexts);
            return MatchHelper.computeMatchingTriples(distanceMap);
        }

        return new ArrayList<>();
    }

    private List<InboundContext> getContextsWithoutComplement(List<InboundContext> inboundContexts,
                                                              List<Triple> matchingTriples) {
        List<InboundContext> unmatchedInboundContexts = new ArrayList<>(inboundContexts);

        for (Triple triple : matchingTriples) {
            unmatchedInboundContexts.remove(triple.getInboundContext());
        }

        return unmatchedInboundContexts;
    }

    private List<Context> getUnmatchedComplements(List<Context> complements,
                                                  List<Triple> matchingTriples) {
        List<Context> unmatchedComplements = new ArrayList<>(complements);

        for (Triple triple : matchingTriples) {
            unmatchedComplements.remove(triple.getContext());
        }

        return unmatchedComplements;
    }
}
