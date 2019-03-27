package lemming.context.inbound;

import lemming.context.Context;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
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
        add(new SubmitButton(this));

        repeatingView = new RepeatingView("repeater");
        add(repeatingView);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        InboundContextPackage contextPackage = getModelObject();

        matchContextsByHash();
        List<String> unmatchedLocations = findUnmatchedLocations(contextPackage);

        for (String location : unmatchedLocations) {
            MultivaluedMap<Integer, InboundContext> groupedContexts = new InboundContextPackageDao()
                    .groupUnmatchedContexts(contextPackage, location);

            for (Integer key : groupedContexts.keySet()) {
                List<InboundContext> inboundContexts = groupedContexts.get(key);
                List<Context> complements = new InboundContextDao().findComplements(inboundContexts);

                // TODO: what now?
                if (complements != null) {
                    List<Triple> matchingTriples = computeMatchingTriples(inboundContexts, complements);
                    List<InboundContext> contextsWithoutComplement = getContextsWithoutComplement(inboundContexts,
                            matchingTriples);
                    List<Context> complementsWithoutContext = getComplementsWithoutContext(complements,
                            matchingTriples);
                    ContextTreeProvider provider = new ContextTreeProvider(matchingTriples,
                            complementsWithoutContext);

                    repeatingView.add(new ContextTreePanel(repeatingView.newChildId(), location, provider,
                            contextsWithoutComplement));
                }
            }
        }

        // TODO: show something else
        if (repeatingView.size() == 0) {
            repeatingView.setVisible(false);
        }
    }

    /**
     * Match contexts by hash.
     */
    private void matchContextsByHash() {
        InboundContextPackage contextPackage = getModelObject();
        new InboundContextPackageDao().matchContextsByHash(contextPackage);
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

    private List<Context> getComplementsWithoutContext(List<Context> complements,
                                                       List<Triple> matchingTriples) {
        List<Context> unmatchedComplements = new ArrayList<>(complements);

        for (Triple triple : matchingTriples) {
            unmatchedComplements.remove(triple.getContext());
        }

        return unmatchedComplements;
    }

    private class SubmitButton extends AjaxButton {

        SubmitButton(Form<InboundContextPackage> form) {
            super("submitButton", form);
        }

        /**
         * Called on form submit.
         *
         * @param target target that produces an Ajax response
         * @param form the parent form
         */
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            super.onSubmit(target, form);
        }
    }
}
