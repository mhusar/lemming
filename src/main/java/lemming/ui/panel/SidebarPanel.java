package lemming.ui.panel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

/**
 * A collapsible sidebar panel.
 */
public class SidebarPanel extends Panel {
    /**
     * Expanded state of the sidebar.
     */
    private Boolean expanded = false;
    /**
     * Orientation of the sidebar.
     */
    private Orientation orientation;
    /**
     * Sidebar container.
     */
    private MarkupContainer sidebar;

    /**
     * Creates a sidebar panel.
     *
     * @param id          ID of the sidebar panel
     * @param orientation orientation of the sidebar panel
     */
    public SidebarPanel(String id, Orientation orientation) {
        super(id);
        sidebar = new WebMarkupContainer("sidebar");
        this.orientation = orientation;

        if (orientation.equals(Orientation.LEFT)) {
            sidebar.add(AttributeModifier.append("class", "sidebar sidebar-left"));
        } else if (orientation.equals(Orientation.RIGHT)) {
            sidebar.add(AttributeModifier.append("class", "sidebar sidebar-right"));
        }

        add(sidebar);
    }

    /**
     * Renders to the web response what the component wants to contribute.
     *
     * @param response response object
     */
    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        ResourceReference javaScriptReference = new JavaScriptResourceReference(SidebarPanel.class,
                "scripts/sidebar.js");
        ResourceReference styleReference = new CssResourceReference(SidebarPanel.class, "styles/sidebar.css");
        response.render(JavaScriptHeaderItem.forReference(javaScriptReference));
        response.render(CssHeaderItem.forReference(styleReference));
    }

    /**
     * Returns the expanded state of the sidebar.
     *
     * @return Expanded state.
     */
    public Boolean isExpanded() {
        return expanded;
    }

    /**
     * Expands or collapses the sidebar depending on its expanded state.
     *
     * @param target target that produces an Ajax response
     */
    public void toggle(AjaxRequestTarget target) {
        if (!expanded) {
            slideIn(target);
        } else {
            slideOut(target);
        }
    }

    /**
     * Slides the sidebar in.
     *
     * @param target target that produces an Ajax response
     */
    public void slideIn(AjaxRequestTarget target) {
        expanded = true;
        target.appendJavaScript(String.format("slideIn(%s);",
                orientation.equals(Orientation.LEFT) ? "Orientation.LEFT" : "Orientation.RIGHT"));
    }

    /**
     * Slides the sidebar out.
     *
     * @param target target that produces an Ajax response
     */
    public void slideOut(AjaxRequestTarget target) {
        expanded = false;
        target.appendJavaScript(String.format("slideOut(%s);",
                orientation.equals(Orientation.LEFT) ? "Orientation.LEFT" : "Orientation.RIGHT"));
    }

    /**
     * Available sidebar orientations.
     */
    public enum Orientation {
        LEFT, RIGHT
    }
}
