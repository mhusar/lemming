package lemming.lemma;

import lemming.ui.page.BasePage;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

/**
 * A page containing a lemma edit form.
 */
@AuthorizeInstantiation({ "SIGNED_IN" })
public class LemmaViewPage extends BasePage {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Model of the edited lemma object.
     */
    private CompoundPropertyModel<Lemma> lemmaModel;

    /**
     * Creates a new lemma edit page.
     *
     * @param lemmaModel
     *            model of the edited lemma object
     */
    public LemmaViewPage(IModel<Lemma> lemmaModel) {
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

        add(new Label("header", getString("LemmaViewPage.header")));
        add(new LemmaViewForm("lemmaViewForm", lemmaModel));
    }
}
