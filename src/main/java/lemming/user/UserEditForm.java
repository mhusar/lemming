package lemming.user;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;

import lemming.lemma.LemmaDao;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.INullAcceptingValidator;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import lemming.HomePage;
import lemming.auth.SignInPage;
import lemming.auth.UserRoles;
import lemming.auth.WebSession;
import lemming.ui.panel.ModalMessagePanel;

/**
 * A form for editing users.
 */
public class UserEditForm extends Form<User> {
    /**
     * Creates a user edit form.
     * 
     * @param id
     *            ID of a user edit form
     * @param model
     *            edited user model
     */
    public UserEditForm(String id, IModel<User> model) {
        super(id, model);
    }

    /**
     * Called when a user edit form is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();

        TextField<String> realNameTextField = new RequiredTextField<>("realName");
        TextField<String> usernameTextField = new RequiredTextField<>("username");
        ListChoice<UserRoles.Role> roleListChoice = new ListChoice<>("role",
                new PropertyModel<>(getModelObject(), "role"),
                new ArrayList<>(Arrays.asList(UserRoles.Role.values())),
                new EnumChoiceRenderer<>(), 3);

        add(realNameTextField.setOutputMarkupId(true));
        add(usernameTextField);
        add(new PasswordTextField("password").setResetPassword(false).setRequired(true));
        add(new CancelButton("cancelButton"));

        if (WebSession.get().getUser().getRole().equals(UserRoles.Role.ADMIN)) {
            CheckBox enabledCheckBox = new CheckBox("enabled");

            add(roleListChoice);
            add(enabledCheckBox);
            add(new DeleteButton("deleteButton", getModel()).setVisible(!(isUserTransient(getModelObject()))));

            if (isUserTransient(getModelObject())) {
                enabledCheckBox.add(AttributeModifier.append("checked","checked"));
            }

            roleListChoice.add(new RequiredRoleValidator());
        }

        realNameTextField.add(new UniqueRealNameValidator(getModel()));
        usernameTextField.add(new UniqueUsernameValidator(getModel()));
    }

    /**
     * Checks if a user is transient.
     * 
     * @param user
     *            user that is checked
     * @return True if a user is transient; false otherwise.
     */
    private Boolean isUserTransient(User user) {
        return new UserDao().isTransient(user);
    }

    /**
     * Called on form submit.
     */
    @Override
    protected void onSubmit() {
        UserDao userDao = new UserDao();
        User user = getModelObject();
        Integer userId = user.getId();
        Boolean passwordChanged = true;

        if (userId != null) {
            User persistentUser = userDao.find(userId);

            if (persistentUser != null) {
                passwordChanged = !user.getPassword().equals(persistentUser.getPassword());
            }
        }

        if (passwordChanged) {
            try {
                byte[] saltBytes = userDao.createRandomSaltBytes();
                String hashedPassword = userDao.hashPassword(user.getPassword(), saltBytes);

                user.setPassword(hashedPassword);
                user.setSalt(saltBytes);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException ignored) {
            }
        }

        if (userDao.isTransient(user)) {
            userDao.persist(user);
        } else {
            userDao.merge(user);
        }

        if (WebSession.get().getUser().getRole().equals(UserRoles.Role.ADMIN)) {
            setResponsePage(UserEditPage.class);
        } else {
            setResponsePage(HomePage.class);
        }
    }

    /**
     * A button which cancels the editing of a user.
     */
    private final class CancelButton extends AjaxLink<User> {
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
            if (WebSession.get().getUser().getRole().equals(UserRoles.Role.ADMIN)) {
                setResponsePage(UserEditPage.class);
            } else {
                setResponsePage(HomePage.class);
            }
        }
    }

    /**
     * A button which deletes a user.
     */
    private final class DeleteButton extends AjaxLink<User> {
        /**
         * Creates a delete button.
         * 
         * @param id
         *            ID of the button
         * @param model
         *            model which is deleted by the button
         */
        private DeleteButton(String id, IModel<User> model) {
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
            // not needed because no data is ownded by a user
            ModalMessagePanel userDeleteDeniedPanel = (ModalMessagePanel) getPage().get("userDeleteDeniedPanel");

            if (!new LemmaDao().findByUser(getModelObject()).isEmpty()) {
                userDeleteDeniedPanel.show(target, getModel());
            } else if (getModelObject().equals(WebSession.get().getUser())) {
                new UserDao().remove(getModelObject());
                WebSession.get().invalidate();
                setResponsePage(SignInPage.class);
            } else {
                new UserDao().remove(getModelObject());
                setResponsePage(UserEditPage.class);
            }
        }
    }

    /**
     * Checks if a user has a role.
     */
    private class RequiredRoleValidator implements
            INullAcceptingValidator<UserRoles.Role> {
        /**
         * Validates value of a form component.
         * 
         * @param validatable
         *            IValidatable instance that is validated
         */
        @Override
        public void validate(IValidatable<UserRoles.Role> validatable) {
            ValidationError error = new ValidationError();

            if (!(validatable.getValue() != null)) {
                error.addKey("UserEditForm.role-is-required");
            }

            if (!(error.getKeys().isEmpty())) {
                validatable.error(error);
            }
        }
    }

    /**
     * Validates a users’s realName against other existent users.
     */
    private class UniqueRealNameValidator implements IValidator<String> {
         /**
         * User model that is edited.
         */
        private IModel<User> userModel;

        /**
         * Creates a realName validator.
         * 
         * @param model
         *            user model that is edited
         */
        public UniqueRealNameValidator(IModel<User> model) {
            userModel = model;
        }

        /**
         * Validates value of a form component.
         * 
         * @param validatable
         *            IValidatable instance that is validated
         */
        @Override
        public void validate(IValidatable<String> validatable) {
            ValidationError error = new ValidationError();
            UserDao userDao = new UserDao();
            User user = userDao.findByRealName(validatable.getValue());

            if (userDao.isTransient(userModel.getObject())) {
                if (user != null) {
                    error.addKey("UserEditForm.realName-is-non-unique");
                }
            } else if (user != null) {
                if (!(user.equals(userModel.getObject()))) {
                    error.addKey("UserEditForm.realName-is-non-unique");
                }
            }

            if (!(error.getKeys().isEmpty())) {
                validatable.error(error);
            }
        }
    }

    /**
     * Validates a users’s username against other existent users.
     */
    private class UniqueUsernameValidator implements IValidator<String> {
        /**
         * User model that is edited.
         */
        private IModel<User> userModel;

        /**
         * Creates a username validator.
         * 
         * @param model
         *            user model that is edited
         */
        public UniqueUsernameValidator(IModel<User> model) {
            userModel = model;
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
            UserDao userDao = new UserDao();
            User user = userDao.findByUsername(validatable.getValue());

            if (userDao.isTransient(userModel.getObject())) {
                if (user != null) {
                    error.addKey("UserEditForm.username-is-non-unique");
                }
            } else if (user != null) {
                if (!(user.equals(userModel.getObject()))) {
                    error.addKey("UserEditForm.username-is-non-unique");
                }
            }

            if (!(error.getKeys().isEmpty())) {
                validatable.error(error);
            }
        }
    }
}
