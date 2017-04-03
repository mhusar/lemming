package lemming.ui.panel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
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
    public ModalFormPanel(String id) {
        super(id);
        modalWindowId = id + "-window";
        form = new Form("form");

        container = new WebMarkupContainer("modalWindow");
        confirmButton = new ConfirmButton("confirmButton", form);
        CancelButton cancelButton = new CancelButton("cancelButton");

        // bring autocomplete ui to front
        container.add(AttributeModifier.append("class", "ui-front"));
        container.setMarkupId(modalWindowId);
        container.add(form);
        container.add(cancelButton);
        container.add(confirmButton.setOutputMarkupId(true));
        form.setDefaultButton(confirmButton);
        add(container);
    }

    /**
     * Called when a modal window panel is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        container.add(new Label("modalTitle", getTitleString()));
        confirmButton.add(new Label("modalConfirm", getConfirmationString()));
        setRenderBodyOnly(true);
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
                + "jQuery(this).find('.form-control:text:visible').first().focus(); })"
                + ".on('hidden.bs.modal', function () { "
                + "jQuery('input[autofocus]').first().focus(); }); "
                + "jQuery('#" + modalWindowId + "').on('keydown', function (event) { "
                + "event.stopPropagation(); });";
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
