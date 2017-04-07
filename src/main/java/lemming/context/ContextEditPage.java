package lemming.context;

import lemming.auth.WebSession;
import lemming.ui.TitleLabel;
import lemming.ui.page.BasePage;
import lemming.ui.panel.FeedbackPanel;
import lemming.ui.panel.ModalMessagePanel;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

/**
 * A page containing a context edit form.
 */
@AuthorizeInstantiation({ "SIGNED_IN" })
public class ContextEditPage extends BasePage {
    /**
     * Model of the edited context object.
     */
    private CompoundPropertyModel<Context> contextModel;

    /**
     * Class of the next page.
     */
    private final Class<? extends Page> nextPageClass;

    /**
     * Creates a context edit page.
     */
    public ContextEditPage() {
        contextModel = new CompoundPropertyModel<>(new Context());
        nextPageClass = null;
    }

    /**
     * Creates a context edit page.
     * 
     * @param nextPageClass
     *            class of the next page
     */
    public ContextEditPage(Class<? extends Page> nextPageClass) {
        contextModel = new CompoundPropertyModel<>(new Context());
        this.nextPageClass = nextPageClass;
    }

    /**
     * Creates a context edit page.
     * 
     * @param contextModel
     *            model of the edited context object
     * @param nextPageClass
     *            class of the next page
     */
    public ContextEditPage(IModel<Context> contextModel, Class<? extends Page> nextPageClass) {
        this.nextPageClass = nextPageClass;

        if (contextModel != null) {
            Context context = contextModel.getObject();
            Context refreshedContext = new ContextDao().refresh(context);
            this.contextModel = new CompoundPropertyModel<>(refreshedContext);
        }
    }

    /**
     * Called when a context edit page is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();

        // check if the session is expired
        WebSession.get().checkSessionExpired();
        ModalMessagePanel contextDeleteConfirmPanel;

        if (nextPageClass != null) {
            contextDeleteConfirmPanel = new ContextDeleteConfirmPanel(nextPageClass);
        } else {
            contextDeleteConfirmPanel = new ContextDeleteConfirmPanel(
                    ContextIndexPage.class);
        }

        add(contextDeleteConfirmPanel);

        if (new ContextDao().isTransient(contextModel.getObject())) {
            contextDeleteConfirmPanel.setVisible(false);
            add(new TitleLabel(getString("ContextEditPage.newHeader")));
            add(new Label("header", getString("ContextEditPage.newHeader")));
        } else {
            add(new TitleLabel(getString("ContextEditPage.editHeader")));
            add(new Label("header", getString("ContextEditPage.editHeader")));
        }

        add(new FeedbackPanel());
        add(new ContextEditForm(contextModel, nextPageClass));
    }
}
