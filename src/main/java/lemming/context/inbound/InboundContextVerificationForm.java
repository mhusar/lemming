package lemming.context.inbound;

import lemming.context.Context;
import lemming.context.ContextDao;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import java.util.List;
import java.util.SortedMap;

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

        private void alignContextsStep2() {
            ContextDao contextDao = new ContextDao();
            InboundContextDao inboundContextDao = new InboundContextDao();
            InboundContextPackageDao inboundContextPackageDao = new InboundContextPackageDao();
            InboundContextPackage contextPackage = (InboundContextPackage) getForm().getModelObject();
            List<InboundContext> allContexts = inboundContextPackageDao.getContexts(contextPackage);
            List<InboundContext> unmatchedContexts = inboundContextPackageDao.findUnmatchedContexts(contextPackage);

            System.err.println(allContexts.size());
            System.err.println(unmatchedContexts.size());
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            super.onSubmit(target, form);

            matchContextsByHash();
            alignContextsStep2();
        }
    }
}
