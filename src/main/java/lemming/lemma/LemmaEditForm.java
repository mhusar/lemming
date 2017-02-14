package lemming.lemma;

import lemming.context.ContextDao;
import lemming.data.Source;
import lemming.pos.PosAutoCompleteTextField;
import lemming.pos.PosTextField;
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

import lemming.ui.panel.ModalMessagePanel;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A form for editing lemmata.
 */
public class LemmaEditForm extends Form<Lemma> {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Class of the next page.
     */
    private Class<? extends Page> nextPageClass;

    /**
     * Creates a lemma edit form.
     * 
     * @param id
     *            ID of the edit form
     * @param model
     *            lemma model that is edited
     * @param nextPageClass
     *            class of the next page
     */
    public LemmaEditForm(String id, IModel<Lemma> model, Class<? extends Page> nextPageClass) {
        super(id, model);

        this.nextPageClass = nextPageClass;
        RequiredTextField<String> nameTextField = new RequiredTextField<String>("name");
        MarkupContainer replacementContainer = new WebMarkupContainer("replacementContainer");
        LemmaTextField replacementTextField = new LemmaAutoCompleteTextField("replacement");
        MarkupContainer posLabel = new WebMarkupContainer("posLabel");
        PosTextField posTextField = new PosAutoCompleteTextField("pos");
        TextField<String> posStringTextField = new TextField<String>("posString");
        ListChoice<Source.LemmaType> sourceListChoice = new ListChoice<Source.LemmaType>("source",
                new PropertyModel<Source.LemmaType>(getModelObject(), "source"),
                new ArrayList<Source.LemmaType>(Arrays.asList(Source.LemmaType.values())),
                new EnumChoiceRenderer<Source.LemmaType>(), 1);
        MarkupContainer referenceContainer = new WebMarkupContainer("referenceContainer");
        TextField<String> referenceTextField = new TextField<String>("reference");
        DeleteButton deleteButton = new DeleteButton("deleteButton", model);

        add(nameTextField);
        add(replacementContainer);
        replacementContainer.add(replacementTextField);
        add(posLabel);
        add(posTextField);
        add(posStringTextField);
        add(sourceListChoice.setEnabled(false));
        add(referenceContainer);
        referenceContainer.add(referenceTextField);
        add(new CancelButton("cancelButton"));
        add(deleteButton);

        if (isLemmaTransient(model)) {
            deleteButton.setVisible(false);
        }

        if (model.getObject().getSource().equals(Source.LemmaType.TL)) {
            posLabel.add(AttributeModifier.replace("for", "posString"));
            posTextField.setVisible(false);
            deleteButton.setVisible(false);
        } else {
            replacementContainer.setVisible(false);
            posStringTextField.setVisible(false);
            referenceContainer.setVisible(false);
        }

        nameTextField.add(new UniqueLemmaNameValidator(model));
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

        if (nextPageClass instanceof Class) {
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
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a cancel button.
         * 
         * @param id
         *            ID of the button
         */
        public CancelButton(String id) {
            super(id);
        }

        /**
         * Called on button click.
         * 
         * @param target
         *            target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            if (nextPageClass instanceof Class) {
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
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a delete button.
         * 
         * @param id
         *            ID of the button
         * @param model
         *            model which is deleted by the button
         */
        private DeleteButton(String id, IModel<Lemma> model) {
            super(id, model);
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

            if (new ContextDao().findByLemma(getModelObject()).isEmpty()) {
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
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Lemma model that is edited.
         */
        private IModel<Lemma> lemmaModel;

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
                if (lemma instanceof Lemma) {
                    error.addKey("LemmaEditForm.lemma-is-non-unique");
                }
            } else if (lemma instanceof Lemma) {
                if (!(lemma.equals(lemmaModel.getObject()))) {
                    error.addKey("LemmaEditForm.lemma-is-non-unique");
                }
            }

            if (!(error.getKeys().isEmpty())) {
                validatable.error(error);
            }
        }
    }
}
