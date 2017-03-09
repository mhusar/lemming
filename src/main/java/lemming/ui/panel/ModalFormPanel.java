package lemming.ui.panel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import java.util.List;

/**
 * A panel containing a modal window with a form.
 */
public abstract class ModalFormPanel extends Panel {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Parent form a modal form panel is attached to.
     */
    private Form<?> parentForm;

    /**
     * Form of a modal form panel.
     */
    private Form<?> form;

    /**
     * The modal window container.
     */
    private MarkupContainer container;

    /**
     * Confirms the dialog when clicked.
     */
    private Button confirmButton;

    /**
     * ID of the modal window.
     */
    private String modalWindowId;

    /**
     * Creates a modal form panel.
     *
     * @param id ID of the panel
     */
    public ModalFormPanel(String id, Form<?> parentForm) {
        super(id);
        this.parentForm = parentForm;
        form = new Form("form");
        modalWindowId = id + "-window";

        container = new WebMarkupContainer("modalWindow");
        Form<?> submitForm = getSubmitForm();

        // bring autocomplete ui to front
        container.add(AttributeModifier.append("class", "ui-front"));
        container.setMarkupId(modalWindowId);
        container.add(form);
        container.add(new CancelButton("cancelButton"));

        confirmButton = new ConfirmButton("confirmButton", submitForm);
        container.add(confirmButton);
        add(container);
        submitForm.add(new EnterAjaxFormSubmitBehavior());
    }

    /**
     * Called when a modal window panel is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        container.add(new Label("modalTitle", getTitleString()));
        confirmButton.add(new Label("modalConfirm", getConfirmationString()));
    }

    /**
     * Renders to the web response what the panel wants to contribute.
     *
     * @param response response object
     */
    @Override
    public void renderHead(IHeaderResponse response) {
        // stop bootstrap from blocking focus on text fields and focus first text field
        String javaScript = "jQuery('#" + modalWindowId + "')"
                + ".on('shown.bs.modal', function () { "
                + "jQuery(this).find('input:text:visible').first().focus(); "
                + "})"
                + ".on('hidden.bs.modal', function () { "
                + "if (jQuery('input[autofocus]').first().isInViewport()) { "
                + "jQuery('input[autofocus]').first().focus(); "
                + "} });";
        response.render(OnDomReadyHeaderItem.forScript(javaScript));
    }

    /**
     * Returns the markup id of the modal window.
     *
     * @return A string.
     */
    public String getModalWindowId() {
        return modalWindowId;
    }

    /**
     * Returns the submit form.
     *
     * @return A form.
     */
    private Form<?> getSubmitForm() {
        if (parentForm instanceof Form) {
            return parentForm;
        } else {
            return form;
        }
    }

    /**
     * Shows a modal window panel.
     *
     * @param target target that produces an Ajax response
     */
    public void show(AjaxRequestTarget target) {
        target.appendJavaScript("jQuery('#" + modalWindowId + "').modal('show');");
    }

    /**
     * Hides a modal window panel.
     *
     * @param target target that produces an Ajax response
     */
    public void hide(AjaxRequestTarget target) {
        form.visitFormComponents(new IVisitor<FormComponent<?>, List<FormComponent>>() {
            @Override
            public void component(FormComponent<?> formComponent, IVisit<List<FormComponent>> visit) {
                target.appendJavaScript("jQuery('#" + formComponent.getMarkupId() + "').val('');");
            }
        });
        target.appendJavaScript("jQuery('#" + modalWindowId + "').modal('hide');");
    }

    /**
     * Adds a form component to the form and prevents form submission when enter is pressed.
     *
     * @param component component which is added
     */
    public void addFormComponent(Component component) {
        component.add(new PreventEnterFormSubmitBehavior());
        form.add(component);
    }

    /**
     * Returns the button text of the confirmation button.
     *
     * @return A confirmation string.
     */
    public String getConfirmationString() {
        return getString("Action.save");
    }

    /**
     * Returns the title of the modal window.
     *
     * @return A title string.
     */
    public abstract String getTitleString();

    /**
     * Called when the modal dialog is confirmed.
     *
     * @param form form that is submitted
     * @param target target that produces an Ajax response
     */
    public abstract void onConfirm(AjaxRequestTarget target, Form<?> form);

    /**
     * Called when the modal dialog is canceled.
     */
    public void onCancel() {
    }

    /**
     * Defines a behavior for when the enter key is pressed.
     */
    private class EnterAjaxFormSubmitBehavior extends AjaxFormSubmitBehavior {
        public EnterAjaxFormSubmitBehavior() {
            super(getSubmitForm(), "keydown");
        }

        /**
         * Called on submit.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        protected void onSubmit(AjaxRequestTarget target) {
            onConfirm(target, getSubmitForm());
            hide(target);
        }

        /**
         * Updates Ajax attributes.
         *
         * @param attributes attributes of the Ajax request
         */
        @Override
        protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
            super.updateAjaxAttributes(attributes);
            attributes.getAjaxCallListeners().add(new AjaxCallListener() {
                public CharSequence getPrecondition(Component component) {
                    return "if (Wicket.Event.keyCode(attrs.event) === 13) { return true; } else { return false; }";
                }
            });
        }
    }

    /**
     * Prevents form components to submit a form when enter is pressed.
     */
    private class PreventEnterFormSubmitBehavior extends Behavior {

        /**
         * Renders to the web response what the panel wants to contribute.
         *
         * @param component component object
         * @param response response object
         */
        @Override
        public void renderHead(Component component, IHeaderResponse response) {
            String javaScript = "jQuery('#" + component.getMarkupId() + "').keydown("
                    + "function (event) { if (event.which === 13) { event.preventDefault(); } });";
            response.render(OnDomReadyHeaderItem.forScript(javaScript));
        }
    }

    /**
     * Cancels the dialog when clicked.
     */
    private class CancelButton extends AjaxLink<Void> {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a button.
         *
         * @param id ID of button
         */
        public CancelButton(String id) {
            super(id);
        }

        /**
         * Called when the button is clicked.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            onCancel();
            hide(target);
        }
    }

    /**
     * Confirms the dialog when clicked.
     */
    private class ConfirmButton extends AjaxButton {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a button.
         *
         * @param id ID of button
         * @param form form of a modal form panel
         */
        public ConfirmButton(String id, Form<?> form) {
            super(id, form);
        }

        /**
         * Called when the button is clicked.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        public void onSubmit(AjaxRequestTarget target, Form<?> form) {
            onConfirm(target, form);
            hide(target);
        }
    }
}
