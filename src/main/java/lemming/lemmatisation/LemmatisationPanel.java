package lemming.lemmatisation;

import org.apache.wicket.markup.html.panel.Panel;

/**
 * A panel that provides buttons for lemmatisation.
 */
public class LemmatisationPanel extends Panel {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a header panel.
     *
     * @param id ID of the panel
     */
    public LemmatisationPanel(String id) {
        super(id);
    }
}
