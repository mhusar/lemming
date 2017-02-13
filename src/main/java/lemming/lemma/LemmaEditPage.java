package lemming.lemma;

import lemming.data.Source;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import lemming.auth.WebSession;
import lemming.ui.page.BasePage;
import lemming.ui.panel.FeedbackPanel;
import lemming.ui.panel.ModalMessagePanel;

/**
 * A page containing a lemma edit form.
 */
@AuthorizeInstantiation({ "SIGNED_IN" })
public class LemmaEditPage extends BasePage {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Model of the edited lemma object.
     */
    private CompoundPropertyModel<Lemma> lemmaModel;

    /**
     * Class of the next page.
     */
    private Class<? extends Page> nextPageClass;

    /**
     * Creates a lemma edit page.
     */
    public LemmaEditPage() {
        lemmaModel = new CompoundPropertyModel<Lemma>(new Lemma());
        nextPageClass = null;
        // set lemma source to user
        lemmaModel.getObject().setSource(Source.LemmaType.USER);
    }

    /**
     * Creates a lemma edit page.
     * 
     * @param nextPageClass
     *            class of the next page
     */
    public LemmaEditPage(Class<? extends Page> nextPageClass) {
        lemmaModel = new CompoundPropertyModel<Lemma>(new Lemma());
        this.nextPageClass = nextPageClass;
        // set lemma source to user
        lemmaModel.getObject().setSource(Source.LemmaType.USER);
    }

    /**
     * Creates a lemma edit page.
     * 
     * @param lemmaModel
     *            model of the edited lemma object
     * @param nextPageClass
     *            class of the next page
     */
    public LemmaEditPage(IModel<Lemma> lemmaModel, Class<? extends Page> nextPageClass) {
        this.nextPageClass = nextPageClass;

        if (lemmaModel instanceof IModel) {
            Lemma lemma = lemmaModel.getObject();
            this.lemmaModel = new CompoundPropertyModel<Lemma>(lemmaModel);
            new LemmaDao().refresh(lemma);
        }
    }

    /**
     * Called when a lemma edit page is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();

        // check if the session is expired
        WebSession.get().checkSessionExpired(getPageClass());
        ModalMessagePanel lemmaDeleteConfirmPanel;

        if (nextPageClass instanceof Class) {
            lemmaDeleteConfirmPanel = new LemmaDeleteConfirmPanel("lemmaDeleteConfirmPanel", nextPageClass);
        } else {
            lemmaDeleteConfirmPanel = new LemmaDeleteConfirmPanel("lemmaDeleteConfirmPanel", LemmaIndexPage.class);
        }

        add(lemmaDeleteConfirmPanel);

        if (new LemmaDao().isTransient(lemmaModel.getObject())) {
            lemmaDeleteConfirmPanel.setVisible(false);
            add(new Label("header", getString("LemmaEditPage.newHeader")));
        } else {
            add(new Label("header", getString("LemmaEditPage.editHeader")));
        }

        add(new LemmaDeleteDeniedPanel("lemmaDeleteDeniedPanel"));
        add(new FeedbackPanel("feedbackPanel"));
        add(new LemmaEditForm("lemmaEditForm", lemmaModel, nextPageClass));
    }
}
