package lemming.lemma;

import lemming.context.ContextDao;
import lemming.data.Source;
import lemming.pos.PosAutoCompleteTextField;
import lemming.pos.PosTextField;
import lemming.sense.SenseDao;
import lemming.ui.panel.ModalMessagePanel;
import lemming.user.UserTextField;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A form for editing lemmata.
 */
class LemmaEditForm extends Form<Lemma> {
    /**
     * Class of the next page.
     */
    private final Class<? extends Page> nextPageClass;

    /**
     * Creates a lemma edit form.
     *
     * @param model lemma model that is edited
     * @param nextPageClass class of the next page
     */
    public LemmaEditForm(IModel<Lemma> model, Class<? extends Page> nextPageClass) {
        super("lemmaEditForm", model);

        this.nextPageClass = nextPageClass;
        RequiredTextField<String> nameTextField = new RequiredTextField<>("name");
        MarkupContainer replacementContainer = new WebMarkupContainer("replacementContainer");
        LemmaTextField replacementTextField = new LemmaAutoCompleteTextField("replacement");
        MarkupContainer posLabel = new WebMarkupContainer("posLabel");
        PosTextField posTextField = new PosAutoCompleteTextField("pos");
        TextField<String> posStringTextField = new TextField<>("posString");
        ListChoice<Source.LemmaType> sourceListChoice = new ListChoice<>("source",
                new PropertyModel<>(getModelObject(), "source"),
                new ArrayList<>(Arrays.asList(Source.LemmaType.values())),
                new EnumChoiceRenderer<>(), 1);
        MarkupContainer referenceContainer = new WebMarkupContainer("referenceContainer");
        TextField<String> referenceTextField = new TextField<>("reference");
        MarkupContainer userContainer = new WebMarkupContainer("userContainer");
        UserTextField userTextField = new UserTextField("user");
        DeleteButton deleteButton = new DeleteButton(model);

        add(nameTextField);
        add(replacementContainer);
        replacementContainer.add(replacementTextField);
        add(posLabel);
        add(posTextField);
        add(posStringTextField);
        add(sourceListChoice.setEnabled(false));
        add(referenceContainer);
        referenceContainer.add(referenceTextField);
        add(userContainer);
        userContainer.add(userTextField);

        add(new CancelButton());
        add(deleteButton);

        if (isLemmaTransient(model)) {
            deleteButton.setVisible(false);
        }

        if (model.getObject().getSource().equals(Source.LemmaType.TL)) {
            nameTextField.setEnabled(false);
            posLabel.add(AttributeModifier.replace("for", "posString"));
            posTextField.setVisible(false);
            posStringTextField.setEnabled(false);
            referenceTextField.setEnabled(false);
            userContainer.setVisible(false);
            deleteButton.setVisible(false);

            if (model.getObject().getReplacement() != null) {
                if (model.getObject().getReplacement().getSource().equals(Source.LemmaType.TL)) {
                    replacementTextField.setEnabled(false);
                }
            }
        } else {
            replacementContainer.setVisible(false);
            posStringTextField.setVisible(false);
            referenceContainer.setVisible(false);
            userTextField.setEnabled(false);
        }

        nameTextField.add(new UniqueLemmaNameValidator(model));

        // check if a replacement lemma set by a user is a user-generated lemma
        if (replacementContainer.isVisible() && replacementTextField.isEnabled()) {
            replacementTextField.add(new ReplacementLemmaValidator());
        }
    }

    /**
     * Checks if a lemma model is transient.
     * 
     * @param model
     *            lemma model that is checked
     * @return True if a lemma model is transient; false otherwise.
     */
    private Boolean isLemmaTransient(IModel<Lemma> model) {
        return new LemmaDao().isTransient(model.getObject());
    }

    /**
     * Called on form submit.
     */
    @Override
    protected void onSubmit() {
        LemmaDao lemmaDao = new LemmaDao();
        Lemma lemma = getModelObject();

        if (lemmaDao.isTransient(lemma)) {
            lemmaDao.persist(lemma);
        } else {
            lemmaDao.merge(lemma);
        }

        if (nextPageClass != null) {
            setResponsePage(nextPageClass);
        } else {
            setResponsePage(LemmaEditPage.class);
        }
    }

    /**
     * A button which cancels the editing of a lemma.
     */
    private final class CancelButton extends AjaxLink<Lemma> {
        /**
         * Creates a cancel button.
         */
        public CancelButton() {
            super("cancelButton");
        }

        /**
         * Called on button click.
         * 
         * @param target
         *            target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            if (nextPageClass != null) {
                setResponsePage(nextPageClass);
            } else {
                setResponsePage(LemmaIndexPage.class);
            }
        }
    }

    /**
     * A button which deletes a lemma.
     */
    private final class DeleteButton extends AjaxLink<Lemma> {
        /**
         * Creates a delete button.
         *
         * @param model model which is deleted by the button
         */
        private DeleteButton(IModel<Lemma> model) {
            super("deleteButton", model);
        }

        /**
         * Called on button click.
         * 
         * @param target
         *            target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            ModalMessagePanel lemmaDeleteConfirmPanel = (ModalMessagePanel) getPage().get("lemmaDeleteConfirmPanel");
            ModalMessagePanel lemmaDeleteDeniedPanel = (ModalMessagePanel) getPage().get("lemmaDeleteDeniedPanel");

            if (new ContextDao().findByLemma(getModelObject()).isEmpty() &&
                    new SenseDao().findByLemma(getModelObject()).isEmpty()) {
                lemmaDeleteConfirmPanel.show(target, getModel());
            } else {
                lemmaDeleteDeniedPanel.show(target, getModel());
            }
        }
    }

    /**
     * Validates a lemma’s name against other existent lemmata.
     */
    private class UniqueLemmaNameValidator implements IValidator<String> {
        /**
         * Lemma model that is edited.
         */
        private final IModel<Lemma> lemmaModel;

        /**
         * Creates a lemma name validator.
         * 
         * @param model
         *            lemma model that is edited
         */
        public UniqueLemmaNameValidator(IModel<Lemma> model) {
            lemmaModel = model;
        }

        /**
         * Validates the value of a form component.
         * 
         * @param validatable
         *            IValidatable instance that is validated
         */
        @Override
        public void validate(IValidatable<String> validatable) {
            ValidationError error = new ValidationError();
            LemmaDao lemmaDao = new LemmaDao();
            Lemma lemma = lemmaDao.findByName(validatable.getValue());

            if (lemmaDao.isTransient(lemmaModel.getObject())) {
                if (lemma != null) {
                    error.addKey("LemmaEditForm.lemma-is-non-unique");
                }
            } else if (lemma != null) {
                if (!(lemma.equals(lemmaModel.getObject()))) {
                    error.addKey("LemmaEditForm.lemma-is-non-unique");
                }
            }

            if (!(error.getKeys().isEmpty())) {
                validatable.error(error);
            }
        }
    }

    /**
     * Validates a lemma’s replacement lemma.
     */
    private class ReplacementLemmaValidator implements IValidator<Lemma> {
        /**
         * Validates the value of a form component.
         *
         * @param validatable
         *            IValidatable instance that is validated
         */
        @Override
        public void validate(IValidatable<Lemma> validatable) {
            ValidationError error = new ValidationError();
            Lemma lemma = validatable.getValue();

            if (lemma != null) {
                if (lemma.getSource().equals(Source.LemmaType.TL)) {
                    error.addKey("LemmaEditForm.replacement-lemma-is-tl-lemma");
                }
            }

            if (!(error.getKeys().isEmpty())) {
                validatable.error(error);
            }
        }
    }
}
