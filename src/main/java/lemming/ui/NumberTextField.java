package lemming.ui;

import org.apache.wicket.markup.html.form.TextField;

/**
 * A text field for numerical text.
 */
public class NumberTextField extends TextField<Integer> {
    /**
     * Creates a number text field.
     */
    public NumberTextField() {
        super("position");
    }

    /**
     * Returns the input types of a text field.
     *
     * @return A string array with HTML5 input types.
     */
    @Override
    protected String[] getInputTypes() {
        return new String[]{"number"};
    }
}
