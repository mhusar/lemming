package lemming.resource;

import lemming.ui.page.BasePage;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Button;

/**
 * A download page for data resources.
 */
@AuthorizeInstantiation({ "SIGNED_IN" })
public class ResourcePage extends BasePage {
    /**
     * Creates a resource page.
     */
    public ResourcePage() {
        add(new Button("downloadContexts"));
        add(new Button("downloadContextKwicIndex"));
        add(new Button("downloadLemmata"));
        add(new Button("downloadPos"));
    }
}
