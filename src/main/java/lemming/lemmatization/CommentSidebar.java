package lemming.lemmatization;

import lemming.context.Comment;
import lemming.context.Context;
import lemming.context.ContextDao;
import lemming.ui.Overlay;
import lemming.ui.panel.SidebarPanel;
import lemming.user.User;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A sidebar panel displaying comments of contexts.
 */
public class CommentSidebar extends SidebarPanel {
    /**
     * Overlay displayed when the sidebar is visible.
     */
    private final Overlay overlay;
    /**
     * Context model.
     */
    private IModel<Context> model;

    /**
     * Creates a sidebar panel for comments.
     *
     * @param id          ID of the sidebar panel
     * @param orientation orientation of the sidebar panel
     */
    public CommentSidebar(String id, Orientation orientation) {
        super(id, orientation);
        addComponent(new RefreshingView<Comment>("comments") {
            @Override
            protected Iterator<IModel<Comment>> getItemModels() {
                if (model == null) {
                    return new ArrayList<IModel<Comment>>().iterator();
                }

                Context refreshedContext = new ContextDao().refresh(model.getObject());
                List<IModel<Comment>> commentModels = new ArrayList<>();

                for (Comment comment : refreshedContext.getComments()) {
                    commentModels.add(Model.of(comment));
                }

                return commentModels.iterator();
            }

            @Override
            protected void populateItem(Item<Comment> item) {
                Comment comment = item.getModelObject();
                User user = comment.getUser();

                item.add(new Label("user", user.getRealName()));
                item.add(new TextArea<String>("content", new PropertyModel<>(comment, "content")));
            }
        });
        overlay = new Overlay() {
            @Override
            public void onHide(AjaxRequestTarget target) {
                CommentSidebar.super.slideOut(target);
            }
        };
        add(overlay);
        getSidebar().add(AttributeModifier.append("class", "z-index-modal-0"));
        setOutputMarkupId(true);
    }

    /**
     * Slides the sidebar in.
     *
     * @param target target that produces an Ajax response
     */
    @Override
    public void slideIn(AjaxRequestTarget target) {
        super.slideIn(target);
        target.appendJavaScript(String.format("jQuery('#%s').addClass('z-index-modal');",
                overlay.getMarkupId()));
        overlay.show(target);
    }

    /**
     * Slides the sidebar out.
     *
     * @param target target that produces an Ajax response
     */
    @Override
    public void slideOut(AjaxRequestTarget target) {
        super.slideOut(target);
        target.appendJavaScript(String.format("jQuery('#%s').removeClass('z-index-modal');",
                overlay.getMarkupId()));
        overlay.hide(target);
    }

    /**
     * Refreshes the model of RefreshingView.
     *
     * @param model  context model
     * @param target target that produces an Ajax response
     */
    public void refresh(IModel<Context> model, AjaxRequestTarget target) {
        this.model = model;
        target.add(this);
    }
}
