package lemming.ui.page;

import org.apache.wicket.ajax.IAjaxIndicatorAware;

/**
 * A base page with a header panel which displays a piece of markup when an Ajax
 * request is processing.
 */
class OverlayBasePage extends BasePage implements IAjaxIndicatorAware {
    /**
     * Creates an overlay base page.
     */
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
