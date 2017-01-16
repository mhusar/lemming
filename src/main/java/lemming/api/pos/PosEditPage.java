package lemming.api.pos;

import lemming.api.auth.WebSession;
import lemming.api.data.Source;
import lemming.api.ui.page.BasePage;
import lemming.api.ui.panel.FeedbackPanel;
import lemming.api.ui.panel.ModalMessagePanel;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

/**
 * A page containing a part of speech edit form.
 */
@AuthorizeInstantiation({ "SIGNED_IN" })
public class PosEditPage extends BasePage {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Model of the edited pos object.
     */
    private CompoundPropertyModel<Pos> posModel;

    /**
     * Class of the next page.
     */
    private Class<? extends Page> nextPageClass;

    /**
     * Creates a new pos edit page.
     */
    public PosEditPage() {
        posModel = new CompoundPropertyModel<Pos>(new Pos());
        nextPageClass = null;
        // set pos source to user
        posModel.getObject().setSource(Source.PosType.USER);
    }

    /**
     * Creates a new pos edit page.
     * 
     * @param nextPageClass
     *            class of the next page
     */
    public PosEditPage(Class<? extends Page> nextPageClass) {
        posModel = new CompoundPropertyModel<Pos>(new Pos());
        this.nextPageClass = nextPageClass;
        // set pos source to user
        posModel.getObject().setSource(Source.PosType.USER);
    }

    /**
     * Creates a new pos edit page.
     * 
     * @param posModel
     *            model of the edited pos object
     * @param nextPageClass
     *            class of the next page
     */
    public PosEditPage(IModel<Pos> posModel, Class<? extends Page> nextPageClass) {
        this.nextPageClass = nextPageClass;

        if (posModel instanceof IModel) {
            Pos pos = posModel.getObject();
            this.posModel = new CompoundPropertyModel<Pos>(posModel);
            new PosDao().refresh(pos);
        }
    }

    /**
     * Called when a pos edit page is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();

        // check if the session is expired
        WebSession.get().checkSessionExpired(getPageClass());
        ModalMessagePanel posDeleteConfirmPanel;

        if (nextPageClass instanceof Class) {
            posDeleteConfirmPanel = new PosDeleteConfirmPanel("posDeleteConfirmPanel", nextPageClass);
        } else {
            posDeleteConfirmPanel = new PosDeleteConfirmPanel("posDeleteConfirmPanel", PosIndexPage.class);
        }

        add(posDeleteConfirmPanel);

        if (new PosDao().isTransient(posModel.getObject())) {
            posDeleteConfirmPanel.setVisible(false);
            add(new Label("header", getString("PosEditPage.newHeader")));
        } else {
            add(new Label("header", getString("PosEditPage.editHeader")));
        }

        add(new PosDeleteDeniedPanel("posDeleteDeniedPanel"));
        add(new FeedbackPanel("feedbackPanel"));
        add(new PosEditForm("posEditForm", posModel, nextPageClass));
    }
}