package lemming.sense;

import lemming.auth.WebSession;
import lemming.lemma.Lemma;
import lemming.lemma.LemmaDao;
import lemming.ui.TitleLabel;
import lemming.ui.page.BasePage;
import lemming.ui.panel.FeedbackPanel;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * A page for sense editing.
 */
@AuthorizeInstantiation({ "SIGNED_IN" })
public class SenseEditPage extends BasePage {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Model of the parent lemma.
     */
    private IModel<Lemma> lemmaModel;

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
     * @param model model of the edited lemma or sense object
     * @param nextPageClass class of the next page
     */
    public SenseEditPage(IModel<?> model, Class<? extends Page> nextPageClass) {
        this.nextPageClass = nextPageClass;

        if (model instanceof IModel) {
            if (model.getObject() instanceof Lemma) {
                Lemma lemma = (Lemma) model.getObject();
                Lemma refreshedLemma = new LemmaDao().refresh(lemma);
                lemmaModel = new Model<Lemma>(refreshedLemma);
            } else if (model.getObject() instanceof Sense) {
                Sense sense = (Sense) model.getObject();
                Sense refreshedSense = new SenseDao().refresh(sense);
                senseModel = new CompoundPropertyModel<Sense>(refreshedSense);
                lemmaModel = new Model<Lemma>(refreshedSense.getLemma());
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
        WebSession.get().checkSessionExpired();

        add(new TitleLabel(getString("SenseEditPage.editHeader")));
        add(new Label("header", getString("SenseEditPage.editHeader")));
        add(new FeedbackPanel("feedbackPanel"));

        if (senseModel instanceof IModel) {
            add(new SenseEditPanel("senseEditPanel", lemmaModel, senseModel));
        } else {
            add(new SenseEditPanel("senseEditPanel", lemmaModel));
        }
    }
}
