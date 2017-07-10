package lemming.ui;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * An link that submits a form asynchronously.
 */
public class SubmitLink extends AjaxSubmitLink implements IAjaxIndicatorAware {
    /**
     * The page loaded after submit.
     */
    private final Page responsePage;

    /**
     * Creates a submit link.
     * 
     * @param id
     *            ID of the link
     * @param form
     *            form that is submitted
     */
    public SubmitLink(String id, Form<?> form, Page responsePage) {
        super(id, form);
        this.responsePage = responsePage;
    }

    /**
     * Returns the markup ID of the indicating element.
     * 
     * @return A markup ID attribute value.
     */
    @Override
    public String getAjaxIndicatorMarkupId() {
        return "overlay";
    }

    /**
     * Called on form submit.
     * 
     * @param target
     *            target that produces an Ajax response
     * @param form
     *            the form that is submitted
     */
    @Override
    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
        setResponsePage(responsePage);
    }

    /**
     * Called when form submit fails.
     * 
     * @param target
     *            target that produces an Ajax response
     * @param form
     *            the form that is submitted
     */
    @Override
    protected void onError(AjaxRequestTarget target, Form<?> form) {
        Panel feedbackPanel = (Panel) form.get("feedbackPanel");

        target.add(feedbackPanel);
        target.appendJavaScript("setupFeedbackPanel(\"#" + feedbackPanel.getMarkupId() + "\")");
    }
}
