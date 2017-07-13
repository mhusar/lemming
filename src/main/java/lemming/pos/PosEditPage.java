package lemming.pos;

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
 * A page containing a part of speech edit form.
 */
@AuthorizeInstantiation({"SIGNED_IN"})
public class PosEditPage extends BasePage {
    /**
     * Model of the edited pos object.
     */
    private CompoundPropertyModel<Pos> posModel;

    /**
     * Class of the next page.
     */
    private final Class<? extends Page> nextPageClass;

    /**
     * Creates a pos edit page.
     */
    public PosEditPage() {
        posModel = new CompoundPropertyModel<>(new Pos());
        nextPageClass = null;
        // set pos source to user
        posModel.getObject().setSource();
    }

    /**
     * Creates a pos edit page.
     *
     * @param nextPageClass class of the next page
     */
    public PosEditPage(Class<? extends Page> nextPageClass) {
        posModel = new CompoundPropertyModel<>(new Pos());
        this.nextPageClass = nextPageClass;
        // set pos source to user
        posModel.getObject().setSource();
    }

    /**
     * Creates a pos edit page.
     *
     * @param posModel      model of the edited pos object
     * @param nextPageClass class of the next page
     */
    public PosEditPage(IModel<Pos> posModel, Class<? extends Page> nextPageClass) {
        this.nextPageClass = nextPageClass;

        if (posModel != null) {
            Pos pos = posModel.getObject();
            this.posModel = new CompoundPropertyModel<>(posModel);
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
        WebSession.get().checkSessionExpired();
        ModalMessagePanel posDeleteConfirmPanel;

        if (nextPageClass != null) {
            posDeleteConfirmPanel = new PosDeleteConfirmPanel(nextPageClass);
        } else {
            posDeleteConfirmPanel = new PosDeleteConfirmPanel(PosIndexPage.class);
        }

        add(posDeleteConfirmPanel);

        if (new PosDao().isTransient(posModel.getObject())) {
            posDeleteConfirmPanel.setVisible(false);
            add(new TitleLabel(getString("PosEditPage.newHeader")));
            add(new Label("header", getString("PosEditPage.newHeader")));
        } else {
            add(new TitleLabel(getString("PosEditPage.editHeader")));
            add(new Label("header", getString("PosEditPage.editHeader")));
        }

        add(new PosDeleteDeniedPanel());
        add(new FeedbackPanel());
        add(new PosEditForm(posModel, nextPageClass));
    }
}
