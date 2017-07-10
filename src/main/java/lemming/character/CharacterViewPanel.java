package lemming.character;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import lemming.auth.WebSession;
import lemming.ui.AjaxView;

/**
 * A panel with a view which displays special characters.
 */
public class CharacterViewPanel extends Panel {
    /**
     * A view displaying special characters.
     */
    private CharacterAjaxView characterView;

    /**
     * Creates a character view panel.
     * 
     * @param id
     *            ID of the panel
     */
    public CharacterViewPanel(String id) {
        super(id);
        setOutputMarkupId(true);

        this.characterView = new CharacterAjaxView("characterView");
        WebMarkupContainer dummyItem = new WebMarkupContainer("dummyItem");

        dummyItem.setOutputMarkupId(true);
        characterView.setNoItemContainer(dummyItem);
        add(dummyItem);
        add(characterView);
    }

    /**
     * Returns the character view.
     * 
     * @return A character view.
     */
    public AjaxView<Character> getCharacterView() {
        return characterView;
    }

    /**
     * Called when a character view item is clicked.
     * 
     * @param target
     *            target that produces an Ajax response
     * @param model
     *            character model of the clicked character
     */
    @SuppressWarnings("unchecked")
    public void onItemClick(AjaxRequestTarget target, IModel<Character> model) {
        Page characterEditPage = getPage();
        Form<Character> characterEditForm = (Form<Character>) characterEditPage.get("characterEditForm");
        Form<Character> newCharacterEditForm = new CharacterEditForm("characterEditForm",
                new CompoundPropertyModel<>(model), getCharacterView());

        characterEditForm.replaceWith(newCharacterEditForm);
        target.add(newCharacterEditForm);
        target.focusComponent(newCharacterEditForm.get("character"));

        // clear feedback panel
        WebSession.get().clearFeedbackMessages();
        target.add(characterEditPage.get("feedbackPanel"));
    }
}
