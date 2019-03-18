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
         *
         * @param contextPackage package of inbound contexts
         */
        private List<String> findUnmatchedLocations(InboundContextPackage contextPackage) {
            return new InboundContextPackageDao().findUnmatchedContextLocations(contextPackage);
        }

        /**
         * Computes matching triples for a location.
         *
         * @param contextPackage package of inbound contexts
         * @param location location of contexts
         * @return A list of matching triples.
         */
        private List<Triple> computeMatchingTriplets(InboundContextPackage contextPackage, String location) {
            InboundContextPackageDao inboundContextPackageDao = new InboundContextPackageDao();
            MultivaluedMap<Integer, InboundContext> groupedContexts = inboundContextPackageDao
                    .groupUnmatchedContextsByLocation(contextPackage, location);
            List<Triple> allMatchingTriples = new ArrayList<>();

            for (Integer key : groupedContexts.keySet()) {
                List<InboundContext> contexts = groupedContexts.get(key);
                List<Context> complements = new InboundContextDao().findComplements(contexts);

                if (complements != null) {
                    MultivaluedMap<Integer, Triple> tripleMap = MatchHelper
                            .getTriples(contexts, complements);
                    List<Triple> matchingTriples = MatchHelper.computeMatchingTriples(tripleMap);
                    allMatchingTriples.addAll(MatchHelper.sortTriples(matchingTriples));
                }
            }

            return allMatchingTriples;
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
            InboundContextPackage contextPackage = (InboundContextPackage) getForm().getModelObject();

            matchContextsByHash();
            List<String> unmatchedLocations = findUnmatchedLocations(contextPackage);

            for (String location : unmatchedLocations) {
                List<Triple> matchingTriples = computeMatchingTriplets(contextPackage, location);
            }
        }
    }
}
