package lemming.character;

import lemming.auth.WebSession;
import lemming.ui.AjaxView;
import lemming.ui.TitleLabel;
import lemming.ui.page.BasePage;
import lemming.ui.panel.FeedbackPanel;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import java.util.List;

/**
 * A page containing a special character edit form.
 */
@AuthorizeInstantiation({"USER", "ADMIN"})
public class CharacterEditPage extends BasePage {
    /**
     * Model of the edited character object.
     */
    private final CompoundPropertyModel<Character> characterModel;

    /**
     * Creates a character edit page.
     */
    public CharacterEditPage() {
        List<Character> characters = new CharacterDao().getAll();

        // check if the session is expired
        WebSession.get().checkSessionExpired();

        if (characters.isEmpty()) {
            characterModel = new CompoundPropertyModel<>(new Character());
        } else {
            characterModel = new CompoundPropertyModel<>(characters.get(0));
        }
    }

    /**
     * Called when a character edit page is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        CharacterViewPanel characterViewPanel = new CharacterViewPanel();

        add(new TitleLabel(getString("CharacterEditPage.header")));
        add(new FeedbackPanel().setOutputMarkupId(true));
        add(characterViewPanel);
        add(new AddCharacterButton());
        add(new CharacterEditForm(characterModel, characterViewPanel.getCharacterView()));
    }

    /**
     * A button which starts the creation of a new siglum variant.
     */
    private final class AddCharacterButton extends AjaxLink<Character> {
        /**
         * Creates a button.
         */
        private AddCharacterButton() {
            super("addCharacterButton");
        }

        /**
         * Called on button click.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        @SuppressWarnings("unchecked")
        public void onClick(AjaxRequestTarget target) {
            Page characterEditPage = getPage();
            Panel characterViewPanel = (CharacterViewPanel) characterEditPage.get("characterViewPanel");
            AjaxView<Character> characterView = (AjaxView<Character>) characterViewPanel.get("characterView");
            Component characterEditForm = characterEditPage.get("characterEditForm");
            Component newCharacterEditForm = new CharacterEditForm(
                    new CompoundPropertyModel<>(new Character()), characterView);

            characterView.setSelectedModel(new Model<>(new Character()));
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
