package lemming.tree;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * A label for a tree node.
 *
 * @param <T> data type
 */
public abstract class NodeLabel<T> extends Panel {
    /**
     * Createa node label
     *
     * @param id ID of the label
     */
    public NodeLabel(String id) {
        super(id);
        add(new Label("label", getLabelText()));
    }

    /**
     * Returns the text of the label.
     *
     * @return A string.
     */
    protected abstract String getLabelText();

    /**
     * Processes the component tag.
     *
     * @param tag tag to modify
     */
    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        tag.put("class", "node-label");
    }
}
