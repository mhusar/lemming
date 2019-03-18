package lemming.context.inbound;

import lemming.context.Context;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;

/**
 * A form for the verification of inbound contexts.
 */
class InboundContextVerificationForm extends Form<InboundContextPackage> {
    /**
     * Creates a context verify form.
     *
     * @param model model of a group of inbound contexts
     */
    public InboundContextVerificationForm(IModel<InboundContextPackage> model) {
        super("InboundContextVerificationForm", model);
        add(new SubmitButton(this));
    }

    private class SubmitButton extends AjaxButton {

        public SubmitButton(Form<InboundContextPackage> form) {
            super("submitButton", form);
        }

        /**
         * Match contexts by hash.
         */
        private void matchContextsByHash() {
            InboundContextPackage contextPackage = (InboundContextPackage) getForm().getModelObject();
            new InboundContextPackageDao().matchContextsByHash(contextPackage);
        }

        /**
         * Finds and sets locations with unmatched contexts.
         */
        private List<String> findUnmatchedLocations(InboundContextPackage contextPackage) {
            return new InboundContextPackageDao().findUnmatchedContextLocations(contextPackage);
        }

        private List<MatchHelper.Triple> computeMatchingTriplets(InboundContextPackage contextPackage, String location) {
            InboundContextPackageDao inboundContextPackageDao = new InboundContextPackageDao();
            MultivaluedMap<Integer, InboundContext> groupedContexts = inboundContextPackageDao
                    .groupUnmatchedContextsByLocation(contextPackage, location);
            List<MatchHelper.Triple> allMatchingTriples = new ArrayList<>();

            for (Integer key : groupedContexts.keySet()) {
                List<InboundContext> contexts = groupedContexts.get(key);
                List<Context> complements = new InboundContextDao().findComplements(contexts);

                if (complements != null) {
                    MultivaluedMap<Integer, MatchHelper.Triple> tripleMap = MatchHelper
                            .getTriples(contexts, complements);
                    List<MatchHelper.Triple> matchingTriples = MatchHelper.computeMatchingTriples(tripleMap);
                    allMatchingTriples.addAll(matchingTriples);
                }
            }

            return allMatchingTriples;
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            super.onSubmit(target, form);
            InboundContextPackage contextPackage = (InboundContextPackage) getForm().getModelObject();
            List<String> unmatchedLocations;

            matchContextsByHash();
            unmatchedLocations = findUnmatchedLocations(contextPackage);

            for (String location : unmatchedLocations) {
                List<MatchHelper.Triple> matchingTriples = computeMatchingTriplets(contextPackage, location);
            }
        }
    }
}
