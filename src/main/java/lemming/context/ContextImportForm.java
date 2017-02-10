package lemming.context;

import lemming.HomePage;
import lemming.ui.panel.DropzonePanel;
import org.apache.commons.fileupload.FileItem;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.util.lang.Bytes;

import java.io.IOException;
import java.util.List;

/**
 * A form able to upload and import contexts.
 */
public class ContextImportForm extends Form<Void> {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a context import form.
     *
     * @param id ID of the form
     */
    public ContextImportForm(String id) {
        super(id);
        setMultiPart(true);
        setMaxSize(Bytes.megabytes(5));
        setFileMaxSize(Bytes.megabytes(5));
    }

    /**
     * Called when a context import form is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();

        DropzonePanel dropzonePanel = new DropzonePanel("dropzone");
        SubmitButton submitButton = new SubmitButton("submitButton");

        add(dropzonePanel);
        dropzonePanel.registerSubmitListener(submitButton);
        getPage().add(new ToHomePageButton("toHomePageButton"));
        getPage().add(submitButton);
    }

    /**
     * A button which redirects to the home page.
     */
    private final class ToHomePageButton extends AjaxLink<Void> {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a redirect button.
         *
         * @param id
         *            ID of the button
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
     * A submit button implementing the SubmitListener interface of a dropzone panel.
     */
    private class SubmitButton extends Button implements DropzonePanel.SubmitListener {
        /**
         * Creates a submit button.
         *
         * @param id ID of the submit button
         */
        public SubmitButton(String id) {
            super(id);
        }

        /**
         * Called on submit of a dropzone panel.
         *
         * @param target target that produces an Ajax response
         * @param fileItem object representing a file for a form item
         */
        @Override
        public void onSubmit(AjaxRequestTarget target, FileItem fileItem) {
            ContextXmlReader xmlReader = new ContextXmlReader(target);

            try {
                xmlReader.validateXml(fileItem.getInputStream());
                List<Context> contexts = xmlReader.readXml(fileItem.getInputStream());
                new ContextDao().batchPersist(contexts);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
