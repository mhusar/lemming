package lemming.ui.page;

import javax.json.JsonArray;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import lemming.character.CharacterHelper;
import lemming.ui.input.InputPanel;
import lemming.ui.panel.HeaderPanel;

/**
 * A base page with header and input panels.
 */
public class BasePage extends EmptyBasePage {
    /**
     * Creates a base page.
     */
    public BasePage() {
        super();
    }

    /**
     * Creates a base page.
     * 
     * @param model the page model
     */
    public BasePage(IModel<?> model) {
        super(model);
    }

    /**
     * Called when a base page is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        JsonArray characterData = CharacterHelper.getCharacterData();
        String characterDataString = characterData.toString();
        WebMarkupContainer bodyContainer = new TransparentWebMarkupContainer("body");

        bodyContainer.add(AttributeModifier.append("data-characters", characterDataString));
        add(bodyContainer);
        add(new HeaderPanel("headerPanel", getPage().getClass()));
        add(new InputPanel("inputPanel"));
    }
}
