package lemming.lemma;

import lemming.data.Source;
import lemming.ui.TitleLabel;
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
        // set user that created a lemma
        lemmaModel.getObject().setUser(WebSession.get().getUser());
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
        // set user that created a lemma
        lemmaModel.getObject().setUser(WebSession.get().getUser());
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
            Lemma refreshedLemma = new LemmaDao().refresh(lemma);
            this.lemmaModel = new CompoundPropertyModel<Lemma>(refreshedLemma);
        }
    }

    /**
     * Called when a lemma edit page is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();

        // check if the session is expired
        WebSession.get().checkSessionExpired();
        ModalMessagePanel lemmaDeleteConfirmPanel;
        ModalMessagePanel lemmaDeleteDeniedPanel = new LemmaDeleteDeniedPanel("lemmaDeleteDeniedPanel");

        if (nextPageClass instanceof Class) {
            lemmaDeleteConfirmPanel = new LemmaDeleteConfirmPanel("lemmaDeleteConfirmPanel", nextPageClass);
        } else {
            lemmaDeleteConfirmPanel = new LemmaDeleteConfirmPanel("lemmaDeleteConfirmPanel", LemmaIndexPage.class);
        }

        add(lemmaDeleteConfirmPanel);
        add(lemmaDeleteDeniedPanel);

        if (new LemmaDao().isTransient(lemmaModel.getObject())) {
            lemmaDeleteConfirmPanel.setVisible(false);
            lemmaDeleteDeniedPanel.setVisible(false);
            add(new TitleLabel(getString("LemmaEditPage.newHeader")));
            add(new Label("header", getString("LemmaEditPage.newHeader")));
        } else {
            add(new TitleLabel(getString("LemmaEditPage.editHeader")));
            add(new Label("header", getString("LemmaEditPage.editHeader")));

            if (lemmaModel.getObject().getSource().equals(Source.LemmaType.TL)) {
                lemmaDeleteConfirmPanel.setVisible(false);
                lemmaDeleteDeniedPanel.setVisible(false);
            }
        }

        add(new FeedbackPanel("feedbackPanel"));
        add(new LemmaEditForm("lemmaEditForm", lemmaModel, nextPageClass));
    }
}
