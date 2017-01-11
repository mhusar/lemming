package lemming.api.character;

import java.util.Iterator;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.RangeValidator;

import lemming.api.auth.WebSession;
import lemming.api.ui.AjaxView;
import lemming.api.ui.NumberTextField;

/**
 * A form for editing special characters.
 */
public class CharacterEditForm extends Form<Character> {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * A character view displaying special characters.
     */
    private AjaxView<Character> characterView;

    /**
     * Creates a special character edit form.
     *
     * @param id            ID of the edit form
     * @param model         model of the character
     * @param characterView character view displaying special characters
     */
    public CharacterEditForm(String id, IModel<Character> model, AjaxView<Character> characterView) {
        super(id, model);

        this.characterView = characterView;
        Integer numberOfCharacters = new CharacterDao().getAll().size();
        RequiredTextField<String> characterTextField = new RequiredTextField<String>("character");
        NumberTextField positionTextField = new NumberTextField("position");

        add(characterTextField.setOutputMarkupId(true));
        add(positionTextField.setRequired(true));
        add(new CancelButton("cancelButton"));
        add(new SaveButton("saveButton", this));
        add(new DeleteButton("deleteButton", model).setVisible(!(isCharacterTransient(model.getObject()))));

        characterTextField.add(new UniqueCharacterValidator(model));

        if (isCharacterTransient(model.getObject())) {
            positionTextField.add(AttributeModifier.append("value", (numberOfCharacters + 1)));
            positionTextField.add(new RangeValidator<Integer>(1, numberOfCharacters + 1));
        } else {
            positionTextField.add(new RangeValidator<Integer>(1, numberOfCharacters));
        }
    }

    /**
     * Checks if a character is transient.
     *
     * @param character character that is checked
     * @return True if a character is transient; false otherwise.
     */
    private Boolean isCharacterTransient(Character character) {
        return new CharacterDao().isTransient(character);
    }

    /**
     * A button which saves form contents.
     */
    private final class SaveButton extends AjaxButton {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a save button.
         *
         * @param id   ID of the button
         * @param form form that is submitted
         */
        public SaveButton(String id, final Form<Character> form) {
            super(id, form);
        }

        /**
         * Called on form submit.
         *
         * @param target target that produces an Ajax response
         * @param form   the submitted form
         */
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            Panel feedbackPanel = (Panel) form.getPage().get("feedbackPanel");
            CharacterDao characterDao = new CharacterDao();
            Character character = (Character) form.getModelObject();

            if (characterDao.isTransient(character)) {
                characterDao.persist(character);
            } else {
                characterDao.merge(character);
            }

            Character reloadedCharacter = characterDao.findByCharacter(character.getCharacter());
            IModel<Character> reloadedCharacterModel = new Model<Character>(reloadedCharacter);
            Form<Character> newEditForm = new CharacterEditForm("characterEditForm",
                    new CompoundPropertyModel<Character>(reloadedCharacter), characterView);

            this.remove();
            characterView.setSelectedModel(reloadedCharacterModel);
            characterView.refresh(target);
            target.add(feedbackPanel);
            form.replaceWith(newEditForm);
            target.add(newEditForm);
            target.focusComponent(newEditForm.get("character"));
        }

        /**
         * Called when form submit fails.
         *
         * @param target target that produces an Ajax response
         * @param form   the submitted form
         */
        @Override
        protected void onError(AjaxRequestTarget target, Form<?> form) {
            Panel feedbackPanel = (Panel) form.getPage().get("feedbackPanel");

            target.add(feedbackPanel);
            target.appendJavaScript("setupFeedbackPanel(\"#" + feedbackPanel.getMarkupId() + "\")");
        }
    }

    /**
     * A button which cancels the editing of a character.
     */
    private final class CancelButton extends AjaxLink<Void> {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a cancel button.
         *
         * @param id ID of the button
         */
        public CancelButton(String id) {
            super(id);
        }

        /**
         * Called on button click.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        @SuppressWarnings("unchecked")
        public void onClick(AjaxRequestTarget target) {
            Form<Character> editForm = findParent(CharacterEditForm.class);
            Form<Character> newEditForm = null;
            Panel feedbackPanel = (Panel) editForm.getPage().get("feedbackPanel");
            Iterator<Component> iterator = characterView.iterator();

            if (iterator.hasNext()) {
                Item<Character> item = (Item<Character>) iterator.next();
                IModel<Character> firstCharacterModel = item.getModel();
                newEditForm = new CharacterEditForm("characterEditForm",
                        new CompoundPropertyModel<Character>(firstCharacterModel), characterView);

                characterView.setSelectedModel(firstCharacterModel);
            } else {
                newEditForm = new CharacterEditForm("characterEditForm",
                        new CompoundPropertyModel<Character>(new Character()), characterView);

                characterView.setSelectedModel(new Model<Character>(new Character()));
            }

            this.remove();
            characterView.refresh(target);
            editForm.replaceWith(newEditForm);
            target.add(newEditForm);
            target.focusComponent(newEditForm.get("character"));

            // clear feedback panel
            WebSession.get().clearFeedbackMessages();
            target.add(feedbackPanel);
        }
    }

    /**
     * A button which deletes a siglum variant.
     */
    private final class DeleteButton extends AjaxLink<Character> {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a delete button.
         *
         * @param id    ID of the button
         * @param model model which is deleted by the button
         */
        private DeleteButton(String id, IModel<Character> model) {
            super(id, model);
        }

        /**
         * Called on button click.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        @SuppressWarnings("unchecked")
        public void onClick(AjaxRequestTarget target) {
            Iterator<Component> iterator = characterView.iterator();
            Integer index = 0;
            IModel<Character> selectedCharacterModel = null;

            while (iterator.hasNext()) {
                Item<Character> item = (Item<Character>) iterator.next();

                if (getModelObject().equals(item.getModelObject())) {
                    index = item.getIndex();
                }
            }

            if (index > 0) {
                selectedCharacterModel = ((Item<Character>) characterView.get(index - 1)).getModel();
            } else if (characterView.size() > 1) {
                selectedCharacterModel = ((Item<Character>) characterView.get(1)).getModel();
            } else {
                selectedCharacterModel = new Model<Character>(new Character());
            }

            new CharacterDao().remove(getModelObject());

            Form<Character> editForm = findParent(CharacterEditForm.class);
            Form<Character> newEditForm = new CharacterEditForm("characterEditForm",
                    new CompoundPropertyModel<Character>(selectedCharacterModel), characterView);
            Panel feedbackPanel = (Panel) editForm.getPage().get("feedbackPanel");

            this.remove();
            characterView.setSelectedModel(selectedCharacterModel);
            characterView.refresh(target);
            editForm.replaceWith(newEditForm);
            target.add(newEditForm);
            target.focusComponent(newEditForm.get("character"));

            // clear feedback panel
            WebSession.get().clearFeedbackMessages();
            target.add(feedbackPanel);
        }
    }

    /**
     * Validates a character against other existent characters.
     */
    private class UniqueCharacterValidator implements IValidator<String> {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Character model that is edited.
         */
        private IModel<Character> characterModel;

        /**
         * Creates a character validator.
         *
         * @param model character model that is edited
         */
        public UniqueCharacterValidator(IModel<Character> model) {
            characterModel = model;
        }

        /**
         * Validates the value of a form component.
         *
         * @param validatable IValidatable instance that is validated
         */
        @Override
        public void validate(IValidatable<String> validatable) {
            ValidationError error = new ValidationError();
            CharacterDao characterDao = new CharacterDao();
            String characterString = validatable.getValue();
            Character character = characterDao.findByCharacter(characterString);

            if (characterDao.isTransient(characterModel.getObject())) {
                if (character instanceof Character) {
                    error.addKey("CharacterEditPage.character-is-non-unique");
                }
            } else if (character instanceof Character) {
                if (!(character.equals(characterModel.getObject()))) {
                    error.addKey("CharacterEditPage.character-is-non-unique");
                }
            }

            if (!(error.getKeys().isEmpty())) {
                validatable.error(error);
            }
        }
    }
}
