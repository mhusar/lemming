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
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an input panel.
     * 
     * @param id ID of the input panel
     */
    public InputPanel(String id) {
        super(id);
    }

    /**
     * Renders what the component wants to contribute to the head section.
     * 
     * @param response the response object
     */
    @Override
    public void renderHead(IHeaderResponse response) {
        PackageResourceReference inputPanelScript = new JavaScriptResourceReference(InputPanel.class,
                "scripts/inputpanel.js", getLocale(), getStyle(), "") {
            /**
             * Determines if a deserialized file is compatible with this class.
             */
            private static final long serialVersionUID = 1L;

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
