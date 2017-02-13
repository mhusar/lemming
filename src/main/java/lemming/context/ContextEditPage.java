package lemming.context;

import lemming.auth.WebSession;
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
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Model of the edited context object.
     */
    private CompoundPropertyModel<Context> contextModel;

    /**
     * Class of the next page.
     */
    private Class<? extends Page> nextPageClass;

    /**
     * Creates a context edit page.
     */
    public ContextEditPage() {
        contextModel = new CompoundPropertyModel<Context>(new Context());
        nextPageClass = null;
    }

    /**
     * Creates a context edit page.
     * 
     * @param nextPageClass
     *            class of the next page
     */
    public ContextEditPage(Class<? extends Page> nextPageClass) {
        contextModel = new CompoundPropertyModel<Context>(new Context());
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

        if (contextModel instanceof IModel) {
            Context context = contextModel.getObject();
            this.contextModel = new CompoundPropertyModel<Context>(contextModel);
            new ContextDao().refresh(context);
        }
    }

    /**
     * Called when a context edit page is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();

        // check if the session is expired
        WebSession.get().checkSessionExpired(getPageClass());
        ModalMessagePanel contextDeleteConfirmPanel;

        if (nextPageClass instanceof Class) {
            contextDeleteConfirmPanel = new ContextDeleteConfirmPanel("contextDeleteConfirmPanel", nextPageClass);
        } else {
            contextDeleteConfirmPanel = new ContextDeleteConfirmPanel("contextDeleteConfirmPanel",
                    ContextIndexPage.class);
        }

        add(contextDeleteConfirmPanel);

        if (new ContextDao().isTransient(contextModel.getObject())) {
            contextDeleteConfirmPanel.setVisible(false);
            add(new Label("header", getString("ContextEditPage.newHeader")));
        } else {
            add(new Label("header", getString("ContextEditPage.editHeader")));
        }

        add(new FeedbackPanel("feedbackPanel"));
        add(new ContextEditForm("contextEditForm", contextModel, nextPageClass));
    }
}
