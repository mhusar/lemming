package lemming.sense;

import lemming.auth.WebSession;
import lemming.lemma.LemmaDao;
import lemming.ui.page.BasePage;
import lemming.ui.panel.FeedbackPanel;
import lemming.ui.panel.ModalMessagePanel;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * A page containing a sense edit form.
 */
@AuthorizeInstantiation({ "SIGNED_IN" })
public class SenseEditPage extends BasePage {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Model of the edited sense object.
     */
    private IModel<SenseWrapper> senseWrapperModel;

    /**
     * Class of the next page.
     */
    private Class<? extends Page> nextPageClass;

    /**
     * Creates a new sense edit page.
     * 
     * @param senseWrapperModel
     *            model of the edited sense wrapper object
     * @param nextPageClass
     *            class of the next page
     */
    public SenseEditPage(IModel<SenseWrapper> senseWrapperModel, Class<? extends Page> nextPageClass) {
        this.nextPageClass = nextPageClass;

        if (senseWrapperModel instanceof IModel) {
            this.senseWrapperModel = senseWrapperModel;
            SenseWrapper senseWrapper = senseWrapperModel.getObject();
            senseWrapper.setLemma(new LemmaDao().refresh(senseWrapper.getLemma()));

            if (senseWrapper.getSense() instanceof Sense) {
                senseWrapper.setSense(new SenseDao().refresh(senseWrapper.getSense()));
            }
        }
    }

    /**
     * Called when a sense edit page is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();

        // check if the session is expired
        WebSession.get().checkSessionExpired(getPageClass());
        ModalMessagePanel senseDeleteConfirmPanel;

        if (nextPageClass instanceof Class) {
            senseDeleteConfirmPanel = new SenseDeleteConfirmPanel("senseDeleteConfirmPanel", nextPageClass);
        } else {
            senseDeleteConfirmPanel = new SenseDeleteConfirmPanel("senseDeleteConfirmPanel", SenseIndexPage.class);
        }

        add(senseDeleteConfirmPanel);
        add(new Label("header", getString("SenseEditPage.editHeader")));
        add(new FeedbackPanel("feedbackPanel"));
        add(new SenseEditForm("senseEditForm", senseWrapperModel, nextPageClass));
    }
}
