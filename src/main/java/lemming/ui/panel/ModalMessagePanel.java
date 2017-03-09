package lemming.ui.panel;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import lemming.table.GenericDataTable;

/**
 * A panel containing a modal window with different message dialog types.
 */
public abstract class ModalMessagePanel extends Panel {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Available dialog types.
     */
    public enum DialogType {
        YES_NO, OKAY
    };

    /**
     * Dialog type of the modal window.
     */
    private DialogType dialogType;

    /**
     * Message label of the modal window dialog.
     */
    private Label messageLabel;

    /**
     * The page loaded on confirmation.
     */
    private Page responsePage;

    /**
     * The class of page loaded on confirmation.
     */
    private Class<? extends Page> responsePageClass;

    /**
     * Data table that is refreshed.
     */
    private DataTable<?, String> dataTable;

    /**
     * ID of the modal window.
     */
    private String modalWindowId;

    /**
     * Creates a modal window.
     * 
     * @param id
     *            ID of the panel
     * @param dialogType
     *            dialog type of the modal window
     */
    public ModalMessagePanel(String id, DialogType dialogType) {
        this(id, dialogType, (Page) null);
    }

    /**
     * Creates a modal window.
     * 
     * @param id
     *            ID of the panel
     * @param dialogType
     *            dialog type of the modal window
     * @param responsePage
     *            page loaded on confirmation
     */
    public ModalMessagePanel(String id, DialogType dialogType, Page responsePage) {
        super(id);

        modalWindowId = id + "-window";
        this.dialogType = dialogType;
        this.responsePage = responsePage;
    }

    /**
     * Creates a modal window.
     * 
     * @param id
     *            ID of the panel
     * @param dialogType
     *            dialog type of the modal window
     * @param responsePageClass
     *            class of page loaded on confirmation
     */
    public ModalMessagePanel(String id, DialogType dialogType, Class<? extends Page> responsePageClass) {
        super(id);

        modalWindowId = id + "-window";
        this.dialogType = dialogType;
        this.responsePageClass = responsePageClass;
    }

    /**
     * Creates a modal window.
     * 
     * @param id
     *            ID of the panel
     * @param dialogType
     *            dialog type of the modal window
     * @param dataTable
     *            data table that is refreshed
     */
    public ModalMessagePanel(String id, DialogType dialogType, DataTable<?, String> dataTable) {
        super(id);

        modalWindowId = id + "-window";
        this.dialogType = dialogType;
        this.dataTable = dataTable;
    }

    /**
     * Called when a modal window panel is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();

        MarkupContainer container = new WebMarkupContainer("modalWindow");
        messageLabel = new Label("modalMessage", "modalMessage");
        Fragment fragment = null;

        messageLabel.setOutputMarkupId(true);
        container.setMarkupId(modalWindowId);
        container.add(new Label("modalTitle", getTitleString()));
        container.add(messageLabel);
        add(container);

        if (dialogType == DialogType.YES_NO) {
            fragment = new Fragment("fragment", "yesNoDialog", this);
            fragment.add(new CancelButton("cancelButton"));
            fragment.add(new ConfirmButton("confirmButton").add(new Label("modalConfirm", getConfirmationString())));
        } else if (dialogType == DialogType.OKAY) {
            fragment = new Fragment("fragment", "okayDialog", this);
            fragment.add(new ConfirmButton("confirmButton").add(new Label("modalConfirm", getConfirmationString())));
        }

        container.add(fragment);
    }

    /**
     * Shows a modal window and replaces the message label.
     * 
     * @param target
     *            target that produces an Ajax response
     * @param model
     *            model of the default object model
     */
    public void show(AjaxRequestTarget target, IModel<?> model) {
        setDefaultModel(model);

        Label label = new Label("modalMessage", getMessageModel());

        label.setOutputMarkupId(true);
        label.setEscapeModelStrings(false);
        messageLabel = (Label) messageLabel.replaceWith(label);
        target.add(label);
        target.appendJavaScript("jQuery('#" + modalWindowId + "').modal('show');");
    }

    /**
     * Shows a modal window and replaces the message label.
     * 
     * @param target
     *            target that produces an Ajax response
     * @param model
     *            model of the default object model
     * @param messageModel
     *            custom message model which overrides the model from method
     *            getMessageModel()
     */
    public void show(AjaxRequestTarget target, IModel<?> model, StringResourceModel messageModel) {
        setDefaultModel(model);
        Label label = new Label("modalMessage", messageModel);

        label.setOutputMarkupId(true);
        label.setEscapeModelStrings(false);
        messageLabel = (Label) messageLabel.replaceWith(label);
        target.add(label);
        target.appendJavaScript("jQuery('#" + modalWindowId + "').modal('show');");
    }

    /**
     * Returns the title of the modal window.
     * 
     * @return A title string.
     */
    public abstract String getTitleString();

    /**
     * Returns the message of the modal dialog.
     * 
     * @return A message string model or null.
     */
    public abstract StringResourceModel getMessageModel();

    /**
     * Returns the button text of the confirmation button.
     * 
     * @return A confirmation string.
     */
    public abstract String getConfirmationString();

    /**
     * Called when the modal dialog is canceled.
     */
    public abstract void onCancel();

    /**
     * Called when the modal dialog is confirmed.
     *
     * @param target target that produces an Ajax response
     */
    public abstract void onConfirm(AjaxRequestTarget target);

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
         * @param id
         *            ID of button
         */
        public CancelButton(String id) {
            super(id);
        }

        /**
         * Called when the button is clicked.
         * 
         * @param target
         *            target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            onCancel();
            target.appendJavaScript("jQuery('#" + modalWindowId + "').modal('hide');");
        }
    }

    /**
     * Confirms the dialog when clicked.
     */
    private class ConfirmButton extends AjaxLink<Void> {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a button.
         * 
         * @param id
         *            ID of button
         */
        public ConfirmButton(String id) {
            super(id);
        }

        /**
         * Called when the button is clicked.
         * 
         * @param target
         *            target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            onConfirm(target);

            if (responsePage instanceof Page) {
                setResponsePage(responsePage);
            } else if (responsePageClass instanceof Class) {
                setResponsePage(responsePageClass);
            } else if (dataTable instanceof GenericDataTable<?>) {
                target.add(dataTable);
                target.appendJavaScript("jQuery('#" + modalWindowId + "').modal('hide');");
            } else {
                target.appendJavaScript("jQuery('#" + modalWindowId + "').modal('hide');");
            }
        }
    }
}
