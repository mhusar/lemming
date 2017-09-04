package lemming.ui;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

/**
 * A markup container used as a clickable overlay.
 */
public abstract class Overlay extends WebMarkupContainer {
    /**
     * Inner container of the overlay.
     */
    MarkupContainer overlayInner;

    /**
     * Creates an overlay.
     */
    public Overlay() {
        super("overlay");
        overlayInner = new WebMarkupContainer("overlayInner");

        add(AttributeModifier.append("class", "overlay hidden"));
        add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                Overlay.this.hide(target);
            }
        });
        overlayInner.add(AttributeModifier.append("class", "overlay-inner"));
        add(overlayInner);
        setOutputMarkupId(true);
    }

    /**
     * Renders to the web response what the component wants to contribute.
     *
     * @param response response object
     */
    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        ResourceReference cssResourceReference = new CssResourceReference(Overlay.class,
                "styles/overlay.css");
        response.render(CssHeaderItem.forReference(cssResourceReference));
    }

    /**
     * Shows the overlay container.
     *
     * @param target target that produces an Ajax response
     */
    public void show(AjaxRequestTarget target) {
        target.appendJavaScript(String.format("jQuery('#%s').addClass('show').removeClass('hidden');", getMarkupId()));
        target.appendJavaScript(String.format("window.setTimeout(function() { jQuery('#%s').addClass('show-overlay')" +
                        ".removeClass('hidden-overlay') }, 150);", getMarkupId()));
    }

    /**
     * Hides the overlay container.
     *
     * @param target target that produces an Ajax response
     */
    public void hide(AjaxRequestTarget target) {
        target.appendJavaScript(String.format("jQuery('#%s').addClass('hidden').removeClass('show');", getMarkupId()));
        target.appendJavaScript(String.format("window.setTimeout(function() { jQuery('#%s').addClass" +
                        "('hidden-overlay').removeClass('show-overlay') }, 150);", getMarkupId()));
        Overlay.this.onHide(target);
    }

    /**
     * Called when an overlay is hidden.
     *
     * @param target target that produces an Ajax response
     */
    public abstract void onHide(AjaxRequestTarget target);
}
