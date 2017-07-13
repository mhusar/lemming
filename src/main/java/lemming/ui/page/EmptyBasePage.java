package lemming.ui.page;

import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.head.*;
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
     * Creates a base page.
     */
    protected EmptyBasePage() {
        super();
    }

    /**
     * Creates a base page.
     *
     * @param model the page model
     */
    protected EmptyBasePage(IModel<?> model) {
        super(model);
    }

    /**
     * Configures the response of a page.
     *
     * @param response response object
     */
    @Override
    protected void configureResponse(WebResponse response) {
        String contentType = AuthenticatedWebApplication.get().getInitParameter("wicket.contentType");

        if (contentType != null) {
            response.setContentType(contentType);
        }
    }

    /**
     * Renders header items to the web response.
     *
     * @param response the response object
     */
    public void renderHead(IHeaderResponse response) {
        PackageResourceReference globalStyle = new CssResourceReference(EmptyBasePage.class, "styles/global.css");
        PackageResourceReference globalScript = new JavaScriptResourceReference(EmptyBasePage.class,
                "scripts/global.js");

        HeaderItem jQueryUiStyleItem = CssUrlReferenceHeaderItem
                .forUrl("/webjars/jquery-ui/1.12.1/jquery-ui.min.css");
        HeaderItem bootstrapStyleItem = CssUrlReferenceHeaderItem
                .forUrl("/webjars/bootstrap/3.3.7-1/css/bootstrap.min.css");
        HeaderItem bootstrapThemeStyleItem = CssUrlReferenceHeaderItem
                .forUrl("/webjars/bootstrap/3.3.7-1/css/bootstrap-theme.min.css");
        CssHeaderItem globalStyleItem = CssHeaderItem.forReference(globalStyle);
        HeaderItem jqueryScriptItem = JavaScriptHeaderItem.forReference(getApplication()
                .getJavaScriptLibrarySettings().getJQueryReference());
        HeaderItem jQueryUiScriptItem = JavaScriptUrlReferenceHeaderItem
                .forUrl("/webjars/jquery-ui/1.12.1/jquery-ui.min.js");
        HeaderItem bootstrapScriptItem = JavaScriptUrlReferenceHeaderItem
                .forUrl("/webjars/bootstrap/3.3.7-1/js/bootstrap.min.js");
        JavaScriptHeaderItem globalScriptItem = JavaScriptHeaderItem.forReference(globalScript);
        StringHeaderItem faviconItem = new StringHeaderItem("<link rel=\"icon\" type=\"image/png\" " +
                "href=\"/favicon.png\" sizes=\"32x32\"/>");

        response.render(jQueryUiStyleItem);
        response.render(bootstrapStyleItem);
        response.render(bootstrapThemeStyleItem);
        response.render(globalStyleItem);
        response.render(jqueryScriptItem);
        response.render(jQueryUiScriptItem);
        response.render(bootstrapScriptItem);
        response.render(globalScriptItem);
        response.render(faviconItem);
    }
}
