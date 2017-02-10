package lemming.character;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import lemming.auth.WebSession;
import lemming.ui.AjaxView;
import lemming.ui.page.BasePage;
import lemming.ui.panel.FeedbackPanel;

/**
 * A page containing a special character edit form.
 */
@AuthorizeInstantiation({ "USER", "ADMIN" })
public class CharacterEditPage extends BasePage {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Model of the edited character object.
     */
    private CompoundPropertyModel<Character> characterModel;

    /**
     * Creates a character edit page.
     */
    public CharacterEditPage() {
        List<Character> characters = new CharacterDao().getAll();

        // check if the session is expired
        WebSession.get().checkSessionExpired(getPageClass());

        if (characters.isEmpty()) {
            characterModel = new CompoundPropertyModel<Character>(new Character());
        } else {
            characterModel = new CompoundPropertyModel<Character>(characters.get(0));
        }
    }

    /**
     * Called when a character edit page is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();

        CharacterViewPanel characterViewPanel = new CharacterViewPanel("characterViewPanel");

        add(new Label("header", getString("CharacterEditPage.header")));
        add(new FeedbackPanel("feedbackPanel").setOutputMarkupId(true));
        add(characterViewPanel);
        add(new AddCharacterButton("addCharacterButton"));
        add(new CharacterEditForm("characterEditForm", characterModel, characterViewPanel.getCharacterView()));
    }

    /**
     * A button which starts the creation of a new siglum variant.
     */
    private final class AddCharacterButton extends AjaxLink<Character> {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a button.
         * 
         * @param id
         *            ID of a button
         */
        private AddCharacterButton(String id) {
            super(id);
        }

        /**
         * Called on button click.
         * 
         * @param target
         *            target that produces an Ajax response
         */
        @Override
        @SuppressWarnings("unchecked")
        public void onClick(AjaxRequestTarget target) {
            Page characterEditPage = getPage();
            Panel characterViewPanel = (CharacterViewPanel) characterEditPage.get("characterViewPanel");
            AjaxView<Character> characterView = (AjaxView<Character>) characterViewPanel.get("characterView");
            Component characterEditForm = characterEditPage.get("characterEditForm");
            Component newCharacterEditForm = new CharacterEditForm("characterEditForm",
                    new CompoundPropertyModel<Character>(new Character()), characterView);

            characterView.setSelectedModel(new Model<Character>(new Character()));
            characterView.refresh(target);
            characterEditForm.replaceWith(newCharacterEditForm);
            target.add(newCharacterEditForm);
            target.focusComponent(newCharacterEditForm.get("character"));

            // clear feedback panel
            WebSession.get().clearFeedbackMessages();
            target.add(characterEditPage.get("feedbackPanel"));
        }
    }
}
