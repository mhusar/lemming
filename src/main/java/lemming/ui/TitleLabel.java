package lemming.ui;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;

/**
 * A label for title tags of pages.
 */
public class TitleLabel extends Label {
    /*
     * Creates a title label.
     */
    public TitleLabel(IModel<?> model) {
        super("title", model);
    }

    /**
     * Creates a title label.
     *
     * @param label text of the label
     */
    public TitleLabel(Serializable label) {
        this(Model.of(label));
    }

    /**
     * Processes the component tag body.
     *
     * @param markupStream markup stream
     * @param openTag the open tag
     */
    @Override
    public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
        String label = getDefaultModelObjectAsString();
        String branding = getString("TitleLabel.branding");
        replaceComponentTagBody(markupStream, openTag, label + branding);
    }
}
