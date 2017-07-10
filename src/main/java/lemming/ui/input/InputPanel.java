package lemming.ui.input;

import java.util.List;

import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import lemming.WebApplication;

/**
 * An input panel for XML tags and special characters.
 */
public class InputPanel extends Panel {
    /**
     * Creates an input panel.
     *
     */
    public InputPanel() {
        super("inputPanel");
    }

    /**
     * Renders what the component wants to contribute to the head section.
     * 
     * @param response the response object
     */
    @Override
    public void renderHead(IHeaderResponse response) {
        PackageResourceReference inputPanelScript = new JavaScriptResourceReference(InputPanel.class,
                "scripts/input-panel.js", getLocale(), getStyle(), "") {
            /**
             * Returns a list of dependent references.
             * 
             * @return A list of dependent references.
             */
            @Override
            public List<HeaderItem> getDependencies() {
                List<HeaderItem> dependencies = super.getDependencies();
                org.apache.wicket.protocol.http.WebApplication application = WebApplication.get();
                ResourceReference jqueryScript = application.getJavaScriptLibrarySettings().getJQueryReference();

                dependencies.add(JavaScriptHeaderItem.forReference(jqueryScript));
                return dependencies;
            }
        };
        JavaScriptHeaderItem inputPanelScriptItem = JavaScriptHeaderItem.forReference(inputPanelScript);

        response.render(inputPanelScriptItem);
    }
}
