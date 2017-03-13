package lemming.table;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * A behavior that shrinks some table columns.
 */
public class AutoShrinkBehavior extends Behavior {
    /**
     * Renders a JavaScript header item to the web response.
     *
     * @param component component which is contributing to the response
     * @param response the response object
     */
    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        PackageResourceReference javaScriptReference = new JavaScriptResourceReference(getClass(),
                "scripts/auto-shrink.js");
        response.render(JavaScriptHeaderItem.forReference(javaScriptReference));
    }
}
