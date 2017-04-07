package lemming.ui.page;

import org.apache.wicket.ajax.IAjaxIndicatorAware;

/**
 * A base page with a header panel which displays a piece of markup when an Ajax
 * request is processing.
 */
public class OverlayBasePage extends BasePage implements IAjaxIndicatorAware {
    /**
     * Creates an overlay base page.
     */
    @SuppressWarnings("unused")
    public OverlayBasePage() {
        super();
    }

    /**
     * Returns the markup ID of the indicating element.
     *
     * @return A markup ID attribute value.
     */
    @Override
    public String getAjaxIndicatorMarkupId() {
        return "overlay";
    }
}
