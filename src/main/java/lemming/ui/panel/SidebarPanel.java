package lemming.ui.panel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.CssResourceReference;
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
     * Available sidebar orientations.
     */
    public enum Orientation {
        LEFT, RIGHT
    }

    /**
     * Sidebar container.
     */
    private MarkupContainer sidebar;

    /**
     * Creates a sidebar panel.
     *
     * @param id ID of the sidebar panel
     * @param orientation orientation of the sidebar panel
     */
    public SidebarPanel(String id, Orientation orientation) {
        super(id);
        sidebar = new WebMarkupContainer("sidebar");

        if (orientation.equals(Orientation.LEFT)) {
            sidebar.add(AttributeModifier.append("class", "sidebar sidebar-left"));
        } else if (orientation.equals(Orientation.RIGHT)) {
            sidebar.add(AttributeModifier.append("class", "sidebar sidebar-right"));
        }

        add(sidebar.setOutputMarkupId(true));
    }

    /**
     * Renders to the web response what the component wants to contribute.
     *
     * @param response  response object
     */
    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        ResourceReference styleReference = new CssResourceReference(SidebarPanel.class, "styles/sidebar.css");
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
        target.appendJavaScript(String.format("jQuery('#%s').addClass('active');", sidebar.getMarkupId()));
    }

    /**
     * Slides the sidebar out.
     *
     * @param target target that produces an Ajax response
     */
    public void slideOut(AjaxRequestTarget target) {
        expanded = false;
        target.appendJavaScript(String.format("jQuery('#%s').removeClass('active');", sidebar.getMarkupId()));
    }
}
