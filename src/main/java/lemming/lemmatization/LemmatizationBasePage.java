package lemming.lemmatization;

import lemming.character.CharacterHelper;
import lemming.ui.page.EmptyBasePage;
import lemming.ui.panel.HeaderPanel;
import lemming.ui.panel.ModalFormPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import javax.json.JsonArray;

/**
 * A lemmatization base page with a header.
 */
public class LemmatizationBasePage extends EmptyBasePage {
    /**
     * Creates a lemmatization base page.
     */
    public LemmatizationBasePage() {
        super();
    }

    /**
     * Creates a lemmatization base page.
     *
     * @param model the page model
     */
    public LemmatizationBasePage(IModel<?> model) {
        super(model);
    }

    /**
     * Called when a lemmatization page is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        JsonArray characterData = CharacterHelper.getCharacterData();
        String characterDataString = characterData.toString();
        WebMarkupContainer bodyContainer = new TransparentWebMarkupContainer("body");

        bodyContainer.add(AttributeModifier.append("data-characters", characterDataString));
        add(bodyContainer);
        add(new HeaderPanel(getPage().getClass()));
    }

    /**
     * Called when an lemmatization page is configured.
     */
    @Override
    protected void onConfigure() {
        super.onConfigure();
        visitChildren(TextField.class, new IVisitor<TextField<?>, Void>() {
            private Integer tabIndex = 1;

            @Override
            public void component(TextField<?> textField, IVisit<Void> visit) {
                if (textField.findParent(ModalFormPanel.class) != null) {
                    return;
                }

                textField.add(AttributeModifier.replace("tabindex", String.valueOf(tabIndex++)));
            }
        });
    }
}
