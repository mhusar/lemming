package lemming.sense;

import lemming.auth.WebSession;
import lemming.lemma.LemmaDao;
import lemming.ui.page.BasePage;
import lemming.ui.panel.FeedbackPanel;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
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
    private IModel<Sense> senseModel;

    /**
     * Class of the next page.
     */
    private Class<? extends Page> nextPageClass;

    /**
     * Creates a sense edit page.
     *
     * @param nextPageClass class of the next page
     */
    public SenseEditPage(Class<? extends Page> nextPageClass) {
        this.nextPageClass = nextPageClass;
    }

    /**
     * Creates a sense edit page.
     * 
     * @param senseModel model of the edited sense object
     * @param nextPageClass class of the next page
     */
    public SenseEditPage(IModel<Sense> senseModel, Class<? extends Page> nextPageClass) {
        this.nextPageClass = nextPageClass;

        if (senseModel instanceof IModel) {
            Sense sense = senseModel.getObject();
            Sense refreshedSense = new SenseDao().refresh(sense);
            this.senseModel = new CompoundPropertyModel<Sense>(refreshedSense);
        }
    }

    /**
     * Called when a sense edit page is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();

        // check if the session is expired
        WebSession.get().checkSessionExpired();

        add(new SenseDeleteConfirmPanel("senseDeleteConfirmPanel"));
        add(new Label("header", getString("SenseEditPage.editHeader")));
        add(new FeedbackPanel("feedbackPanel"));
        add(new SenseEditForm("senseEditForm", senseModel, nextPageClass));
    }
}
