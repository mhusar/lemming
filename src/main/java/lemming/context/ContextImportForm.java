package lemming.context;

import lemming.HomePage;
import lemming.ui.panel.AlertPanel;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.http.servlet.MultipartServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.lang.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A form able to upload and import contexts.
 */
public class ContextImportForm extends Form<Void> {
    /**
     * A logger named corresponding to this class.
     */
    public static final Logger logger = LoggerFactory.getLogger(ContextImportForm.class);

    /**
     * Creates a context import form.
     *
     * @param id ID of the form
     */
    public ContextImportForm(String id) {
        super(id);
        setMultiPart(true);
        setMaxSize(Bytes.megabytes(10));
        setFileMaxSize(Bytes.megabytes(10));
    }

    /**
     * A file input field.
     */
    private FileUploadField fileInput;

    /**
     * A text input field.
     */
    private TextField textInput;

    /**
     * A button which triggers a file open event.
     */
    private Button browseButton;

    /**
     * A button which clears all form components.
     */
    private RemoveButton removeButton;

    /**
     * A panel which displays the import status.
     */
    private AlertPanel alertPanel;

    /**
     * Called when a context import form is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        setMarkupId(getId());
        fileInput = new FileUploadField("fileInput", new Model<ArrayList<FileUpload>>(new ArrayList<FileUpload>()));
        textInput = new TextField<String>("textInput", Model.of(""));
        removeButton = new RemoveButton("removeButton");
        browseButton = new Button("browseButton");
        alertPanel = new AlertPanel("alertPanel");
        SubmitButton submitButton = new SubmitButton("submitButton", this);

        fileInput.add(new FileInputChangeBehavior())
                .add(AttributeModifier.append("style", "position: absolute; left: -9999px;"));
        add(fileInput.setMarkupId(fileInput.getId()));
        add(textInput.setMarkupId(textInput.getId()));
        add(removeButton.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true).setVisible(false));
        add(browseButton.setMarkupId(browseButton.getId()).add(new BrowseButtonBehavior()));
        getPage().add(alertPanel.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true).setVisible(false));
        getPage().add(new ToHomePageButton("toHomePageButton"));
        getPage().add(submitButton);
    }

    private void logException(Exception exception) {
        logger.error("Context import failed with exception.", exception);
    }

    /**
     * Called on submit of the dropzone panel.
     *
     * @param target target that produces an Ajax response
     * @param fileItem object representing a file for a form item
     */
    public void onSubmit(AjaxRequestTarget target, FileItem fileItem) {
        ContextXmlReader xmlReader = new ContextXmlReader();
        List<Context> contexts = null;

        try {
            xmlReader.validateXml(fileItem.getInputStream());
        } catch (IOException | SAXException e) {
            onException(target, e);
            logException(e);
            return;
        }

        try {
            contexts = xmlReader.readXml(fileItem.getInputStream());
        } catch (IOException | XMLStreamException e) {
            onException(target, e);
            logException(e);
            return;
        }

        if (contexts instanceof List) {
            String message;

            if (contexts.size() > 0) {
                StringResourceModel messageModel = new StringResourceModel("ContextImportPage.successMessage", this)
                        .setParameters(String.valueOf(contexts.size()));
                message = messageModel.getString();
                alertPanel.setMessage(message).setType(AlertPanel.Type.SUCCESS).setVisible(true);
                new ContextDao().batchPersist(contexts);
            } else {
                message = getString("ContextImportPage.noContextsMessage");
                alertPanel.setMessage(message).setType(AlertPanel.Type.INFO).setVisible(true);
            }

            target.add(alertPanel);
        }
    }

    /**
     * Called when an exception occurs.
     *
     * @param target target that produces an Ajax response
     * @param exception exception which occurred.
     */
    private void onException(AjaxRequestTarget target, Exception exception) {
        String message = exception.getLocalizedMessage();

        if (exception instanceof SAXParseException) {
            SAXParseException saxParseException = (SAXParseException) exception;

            if (saxParseException.getLineNumber() != -1 && saxParseException.getColumnNumber() != -1) {
                message += "<br/>" + getString("ContextImportPage.line") + ": " + saxParseException.getLineNumber();
                message += ", " + getString("ContextImportPage.column") + ": " +
                        saxParseException.getColumnNumber();
            }
        } else if (exception instanceof ContextXmlReader.XmlStreamException) {
            ContextXmlReader.XmlStreamException xmlStreamException = (ContextXmlReader.XmlStreamException) exception;
            message += "<br/>" + getString("ContextImportPage.line") + ": " +
                    xmlStreamException.getLocation().getLineNumber();
            message += ", " + getString("ContextImportPage.column") + ": " +
                    xmlStreamException.getLocation().getColumnNumber();
        }

        alertPanel.setMessage(message).setType(AlertPanel.Type.ERROR).setVisible(true);
        target.add(alertPanel);
    }

    /**
     * A behavior which syncs the text input field with the file input field.
     */
    private class FileInputChangeBehavior extends AjaxEventBehavior {
        public FileInputChangeBehavior() {
            super("change");
        }

        @Override
        protected void onEvent(AjaxRequestTarget target) {
            String javaScript = "var filename = jQuery('#" + fileInput.getMarkupId() + "')[0].files.length ? " +
                    "jQuery('#" + fileInput.getMarkupId() + "')[0].files[0].name : ''; " +
                    "jQuery('#" + textInput.getMarkupId() + "').val(filename);";
            target.appendJavaScript(javaScript);
            target.add(removeButton.setVisible(true));
            target.add(alertPanel.setVisible(false));
        }
    }

    /**
     * A behavior which triggers a file open event.
     */
    private class BrowseButtonBehavior extends Behavior {
        /**
         * Renders to the web response what the component wants to contribute.
         *
         * @param component component object
         * @param response response object
         */
        @Override
        public void renderHead(Component component, IHeaderResponse response) {
            String javaScript = "jQuery(document).on('click', '#" + browseButton.getMarkupId() + "', function (e) { " +
                    "e.preventDefault(); jQuery('#" + fileInput.getMarkupId() + "')[0].click(); });";
            response.render(OnDomReadyHeaderItem.forScript(javaScript));
        }
    }

    /**
     * A button which clears all form components.
     */
    private class RemoveButton extends AjaxLink<Void> {
        /**
         * Creates a remove button.
         *
         * @param id ID of the button
         */
        public RemoveButton(String id) {
            super(id);
        }

        /**
         * Called on button click.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            setVisible(false);
            target.add(this);
            target.appendJavaScript("jQuery('#" + ContextImportForm.this.getMarkupId() + "')" +
                    ".find('input[type=file], input[type=text]').val('');");
        }
    }

    /**
     * A button which redirects to the home page.
     */
    private final class ToHomePageButton extends AjaxLink<Void> {
        /**
         * Creates a redirect button.
         *
         * @param id ID of the button
         */
        public ToHomePageButton(String id) {
            super(id);
        }

        /**
         * Called on button click.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            setResponsePage(HomePage.class);
        }
    }

    /**
     * An Ajax submit button.
     */
    private class SubmitButton extends IndicatingAjaxButton {
        /**
         * Creates a submit button.
         *
         * @param id ID of the submit button
         * @param form that is submitted
         */
        public SubmitButton(String id, Form<?> form) {
            super(id, form);
        }

        /**
         * Submits a form.
         *
         * @param target target target that produces an Ajax response
         */
        @Override
        protected void onSubmit(AjaxRequestTarget target) {
            ServletWebRequest request = (ServletWebRequest) RequestCycle.get().getRequest();

            try {
                Iterator<String> parameterIterator = request.getRequestParameters().getParameterNames().iterator();

                if (parameterIterator.hasNext()) {
                    MultipartServletWebRequest multipartRequest = request
                            .newMultipartWebRequest(Bytes.megabytes(10), "ignored");
                    multipartRequest.parseFileParts();
                    List<FileItem> fileItems = multipartRequest.getFiles().get(fileInput.getId());

                    if (fileItems != null) {
                        if (fileItems.size() > 0) {
                            ContextImportForm.this.onSubmit(target, fileItems.get(0));
                        }
                    }
                }
            } catch (FileUploadException e) {
                e.printStackTrace();
            }

            removeButton.setVisible(false);
            target.add(removeButton);
            target.appendJavaScript("jQuery('#" + ContextImportForm.this.getMarkupId() + "')" +
                    ".find('input[type=file], input[type=text]').val('');");
        }

        /**
         * Returns the markup ID of the Ajax indicator.
         *
         * @return A component markup id.
         */
        @Override
        public String getAjaxIndicatorMarkupId() {
            return "indicatorOverlayPanel";
        }
    }
}
