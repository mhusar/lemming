package lemming.sense;

import lemming.auth.WebSession;
import lemming.lemma.Lemma;
import lemming.lemma.LemmaDao;
import lemming.ui.TitleLabel;
import lemming.ui.page.BasePage;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

/**
 * A page for sense editing.
 */
@AuthorizeInstantiation({ "SIGNED_IN" })
public class SenseEditPage extends BasePage {
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
     * Empty constructor which is used when a user isn’t signed in.
     */
    public SenseEditPage() {
    }

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

        if (senseModel instanceof IModel) {
            add(new SenseEditPanel("senseEditPanel", lemmaModel, senseModel));
        } else {
            add(new SenseEditPanel("senseEditPanel", lemmaModel));
        }
    }

    /**
     * Renders to the web response what the component wants to contribute.
     *
     * @param response response object
     */
    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        ResourceReference senseEditReference = new JavaScriptResourceReference(SenseEditPage.class,
                "scripts/sense-edit.js");
        response.render(JavaScriptHeaderItem.forReference(senseEditReference));
    }
}
