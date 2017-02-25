package lemming.lemmatization;

import org.apache.wicket.markup.html.panel.Panel;

/**
 * A panel that provides buttons for lemmatization.
 */
public class LemmatizationPanel extends Panel {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a header panel.
     *
     * @param id ID of the panel
     */
    public LemmatizationPanel(String id) {
        super(id);
    }
}
