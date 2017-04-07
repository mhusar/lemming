package lemming.ui.panel;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

/**
 * A panel that displays feedback messages.
 */
public class FeedbackPanel extends org.apache.wicket.markup.html.panel.FeedbackPanel {
    /**
     * Creates a feedback panel.
     */
    public FeedbackPanel() {
        super("feedbackPanel");
        setMarkupId(getId());
    }

    /**
     * Returns a CSS class string.
     *
     * @param message the analyzed feedback message
     * @return A CSS class string based on message level.
     */
    @Override
    protected String getCSSClass(FeedbackMessage message) {
        String css;

        switch (message.getLevel()) {
            case FeedbackMessage.SUCCESS:
                css = "success";
                break;
            case FeedbackMessage.INFO:
                css = "info";
                break;
            case FeedbackMessage.ERROR:
                css = "danger";
                break;
            default:
                css = "warning";
        }

        return css;
    }

    /**
     * Renders to the web response what the component wants to contribute.
     *
     * @param response response object
     */
    @Override
    public void renderHead(IHeaderResponse response) {
        String javaScript = "jQuery('#" + getMarkupId() + " li').each(function (index) { " +
                "var elementClass = jQuery(this).attr('class'); " +
                "jQuery(this).removeClass(elementClass).addClass('alert alert-' + elementClass); });";
        response.render(OnDomReadyHeaderItem.forScript(javaScript));
    }
}
