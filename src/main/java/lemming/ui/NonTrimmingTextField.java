package lemming.ui;

import org.apache.wicket.markup.html.form.TextField;

/**
 * A non-trimming text field.
 */
public class NonTrimmingTextField extends TextField<String> {

    /**
     * Creates a non-trimming text field.
     *
     * @param id id of a text field
     */
    public NonTrimmingTextField(String id) {
        super(id);
    }

    /**
     * Determines whether or not this component should trim its input prior to processing it.
     *
     * @return True if the input should be trimmed.
     */
    @Override
    protected boolean shouldTrimInput() {
        return false;
    }
}
