package lemming.ui.panel;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;

/**
 * A panel that displays feedback messages.
 */
public class FeedbackPanel extends
        org.apache.wicket.markup.html.panel.FeedbackPanel {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a feedback panel.
     * 
     * @param id
     *            ID of a feedback panel.
     */
    public FeedbackPanel(String id) {
        super(id);
    }

    /**
     * Creates a feedback panel.
     * 
     * @param id
     *            ID of a feedback panel.
     * @param filter
     *            a filter for feedback messages
     */
    public FeedbackPanel(String id, IFeedbackMessageFilter filter) {
        super(id, filter);
    }

    /**
     * Returns a CSS class string.
     * 
     * @param message
     *            the analyzed feedback message
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
}
