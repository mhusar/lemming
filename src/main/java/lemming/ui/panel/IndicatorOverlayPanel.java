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
public class IndicatorOverlayPanel extends Panel {
    /**
     * Creates a throbber overlay panel.
     *
     */
    public IndicatorOverlayPanel() {
        super("indicatorOverlayPanel");
        setMarkupId(getId());

        MarkupContainer throbberContainer = new WebMarkupContainer("indicatorContainer");
        PackageResourceReference throbberReference = new PackageResourceReference(getClass(), "images/throbber.svg");
        Image throbberImage = new Image("throbberImage", throbberReference);

        add(AttributeModifier.append("style", "display: none;"));
        add(throbberContainer);
        throbberContainer.add(throbberImage);
        throbberContainer.add(AttributeModifier.append("class", "indicator-overlay"));
    }

    /**
     * Renders a css header item to the web response.
     * 
     * @param response the response object
     */
    @Override
    public void renderHead(IHeaderResponse response) {
        CssResourceReference indicatorCssReference = new CssResourceReference(getClass(),
                "styles/indicator-overlay.css");
        response.render(CssReferenceHeaderItem.forReference(indicatorCssReference));
    }
}
