package lemming.context;

import lemming.context.inbound.InboundContextPackage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

/**
 * A form for the verification of inbound contexts.
 */
class ContextVerificationForm extends Form<InboundContextPackage> {
    /**
     * Creates a context verify form.
     *
     * @param model model of a group of inbound contexts
     */
    public ContextVerificationForm(IModel<InboundContextPackage> model) {
        super("ContextVerificationForm", model);
    }
}
