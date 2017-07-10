package lemming.pos;

import lemming.context.ContextDao;
import lemming.data.Source;
import lemming.lemma.LemmaDao;
import lemming.ui.panel.ModalMessagePanel;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A form for editing parts of speech.
 */
public class PosEditForm extends Form<Pos> {
    /**
     * Class of the next page.
     */
    private final Class<? extends Page> nextPageClass;

    /**
     * Creates a part of speech edit form.
     * 
     * @param id
     *            ID of the edit form
     * @param model
     *            part of speech model that is edited
     * @param nextPageClass
     *            class of the next page
     */
    public PosEditForm(String id, IModel<Pos> model, Class<? extends Page> nextPageClass) {
        super(id, model);

        this.nextPageClass = nextPageClass;
        RequiredTextField<String> nameTextField = new RequiredTextField<>("name");
        ListChoice<Source.PosType> sourceListChoice = new ListChoice<>("source",
                new PropertyModel<>(getModelObject(), "source"),
                new ArrayList<>(Arrays.asList(Source.PosType.values())),
                new EnumChoiceRenderer<>(), 1);

        add(nameTextField);
        add(sourceListChoice.setEnabled(false));
        add(new CancelButton("cancelButton"));
        add(new DeleteButton("deleteButton", model).setVisible(!(isPosTransient(model))));

        nameTextField.add(new UniquePosNameValidator(model));
    }

    /**
     * Checks if a part of speech model is transient.
     * 
     * @param model
     *            part of speech model that is checked
     * @return True if a part of speech model is transient; false otherwise.
     */
    private Boolean isPosTransient(IModel<Pos> model) {
        return new PosDao().isTransient(model.getObject());
    }

    /**
     * Called on form submit.
     */
    @Override
    protected void onSubmit() {
        PosDao posDao = new PosDao();
        Pos pos = getModelObject();

        if (posDao.isTransient(pos)) {
            posDao.persist(pos);
        } else {
            posDao.merge(pos);
        }

        if (nextPageClass != null) {
            setResponsePage(nextPageClass);
        } else {
            setResponsePage(PosEditPage.class);
        }
    }

    /**
     * A button which cancels the editing of a part of speech.
     */
    private final class CancelButton extends AjaxLink<Pos> {
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
            if (nextPageClass != null) {
                setResponsePage(nextPageClass);
            } else {
                setResponsePage(PosIndexPage.class);
            }
        }
    }

    /**
     * A button which deletes a part of speech.
     */
    private final class DeleteButton extends AjaxLink<Pos> {
        /**
         * Creates a delete button.
         * 
         * @param id
         *            ID of the button
         * @param model
         *            model which is deleted by the button
         */
        private DeleteButton(String id, IModel<Pos> model) {
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
            ModalMessagePanel posDeleteConfirmPanel = (ModalMessagePanel) getPage().get("posDeleteConfirmPanel");
            ModalMessagePanel posDeleteDeniedPanel = (ModalMessagePanel) getPage().get("posDeleteDeniedPanel");

            if (new ContextDao().findByPos(getModelObject()).isEmpty() &&
                    new LemmaDao().findByPos(getModelObject()).isEmpty()) {
                posDeleteConfirmPanel.show(target, getModel());
            } else {
                posDeleteDeniedPanel.show(target, getModel());
            }
        }
    }

    /**
     * Validates a part of speech’s name against other existent parts of speech.
     */
    private class UniquePosNameValidator implements IValidator<String> {
        /**
         * Pos model that is edited.
         */
        private final IModel<Pos> posModel;

        /**
         * Creates a part of speech name validator.
         * 
         * @param model
         *            part of speech model that is edited
         */
        public UniquePosNameValidator(IModel<Pos> model) {
            posModel = model;
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
            PosDao posDao = new PosDao();
            Pos pos = posDao.findByName(validatable.getValue());

            if (posDao.isTransient(posModel.getObject())) {
                if (pos != null) {
                    error.addKey("PosEditForm.pos-is-non-unique");
                }
            } else if (pos != null) {
                if (!(pos.equals(posModel.getObject()))) {
                    error.addKey("PosEditForm.pos-is-non-unique");
                }
            }

            if (!(error.getKeys().isEmpty())) {
                validatable.error(error);
            }
        }
    }
}
