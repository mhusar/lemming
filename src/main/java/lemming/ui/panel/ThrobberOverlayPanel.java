package lemming.ui.panel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * An overlay panel showing an animated throbber image.
 */
public class ThrobberOverlayPanel extends Panel {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a throbber overlay panel.
     * 
     * @param id
     *            if of a panel
     */
    public ThrobberOverlayPanel(String id) {
        super(id);
        setOutputMarkupId(true);

        MarkupContainer throbberContainer = new WebMarkupContainer("throbberContainer");
        PackageResourceReference throbberReference = new PackageResourceReference(getClass(), "images/throbber.svg");
        Image throbberImage = new Image("throbberImage", throbberReference);

        add(throbberContainer);
        throbberContainer.add(throbberImage);
        throbberContainer.add(AttributeModifier.append("class", "throbber-overlay initial"));
    }

    /**
     * Renders a css header item to the web response.
     * 
     * @param response
     *            the response object
     */
    @Override
    public void renderHead(IHeaderResponse response) {
        CssResourceReference throbberCssReference = new CssResourceReference(getClass(), "styles/throbber-overlay.css");
        response.render(CssReferenceHeaderItem.forReference(throbberCssReference));
    }

    /**
     * Starts the animation of the throbber overlay.
     */
    public void show() {
        get("throbberContainer").add(AttributeModifier.replace("class", "throbber-overlay animation"));
    }
}
