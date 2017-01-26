package lemming.api.ui.page;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * An empty base page without any panel.
 */
public class EmptyBasePage extends WebPage {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new base page.
     */
    public EmptyBasePage() {
        super();
    }

    /**
     * Creates a new base page.
     * 
     * @param model
     *            the web page’s model
     */
    public EmptyBasePage(IModel<?> model) {
        super(model);
    }

    /**
     * Configures the response of a page.
     *
     * @param response response object
     */
    @Override
    protected void configureResponse(WebResponse response) {
        response.setContentType("application/xhtml+xml");
    }

    /**
     * Renders header items to the web response.
     * 
     * @param response
     *            the response object
     */
    public void renderHead(IHeaderResponse response) {
        PackageResourceReference jQueryUiStyle = new CssResourceReference(EmptyBasePage.class,
                "styles/jquery-ui.css");
        PackageResourceReference bootstrapStyle = new CssResourceReference(EmptyBasePage.class,
                "styles/bootstrap.css");
        PackageResourceReference bootstrapThemeStyle = new CssResourceReference(EmptyBasePage.class,
                "styles/bootstrap-theme.css");
        PackageResourceReference globalStyle = new JavaScriptResourceReference(EmptyBasePage.class,
                "styles/global.css");
        PackageResourceReference jQueryUiScript = new JavaScriptResourceReference(EmptyBasePage.class,
                "scripts/jquery-ui.min.js");
        PackageResourceReference bootstrapScript = new JavaScriptResourceReference(EmptyBasePage.class,
                "scripts/bootstrap.js");
        PackageResourceReference globalScript = new JavaScriptResourceReference(EmptyBasePage.class,
                "scripts/global.js");

        CssHeaderItem jQueryUiStyleItem = CssHeaderItem.forReference(jQueryUiStyle);
        CssHeaderItem bootstrapStyleItem = CssHeaderItem.forReference(bootstrapStyle);
        CssHeaderItem bootstrapThemeStyleItem = CssHeaderItem.forReference(bootstrapThemeStyle);
        CssHeaderItem globalStyleItem = CssHeaderItem.forReference(globalStyle);
        JavaScriptHeaderItem jQueryUiScriptItem = JavaScriptHeaderItem.forReference(jQueryUiScript);
        JavaScriptHeaderItem bootstrapScriptItem = JavaScriptHeaderItem.forReference(bootstrapScript);
        JavaScriptHeaderItem globalScriptItem = JavaScriptHeaderItem.forReference(globalScript);

        response.render(jQueryUiStyleItem);
        response.render(bootstrapStyleItem);
        response.render(bootstrapThemeStyleItem);
        response.render(globalStyleItem);
        response.render(JavaScriptHeaderItem
                .forReference(getApplication().getJavaScriptLibrarySettings().getJQueryReference()));
        response.render(jQueryUiScriptItem);
        response.render(bootstrapScriptItem);
        response.render(globalScriptItem);
    }
}
