package lemming.context;

import lemming.context.inbound.InboundContextGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

/**
 * A form for the verification of inbound contexts.
 */
public class ContextVerificationForm extends Form<InboundContextGroup> {
    /**
     * Creates a context verify form.
     *
     * @param id ID of the form
     * @param model model of a group of inbound contexts
     */
    public ContextVerificationForm(String id, IModel<InboundContextGroup> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
    }
}
