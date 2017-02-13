package lemming.lemma;

import lemming.data.Source;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A form for editing lemmata.
 */
public class LemmaViewForm extends Form<Lemma> {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a lemma edit form.
     *
     * @param id
     *            ID of the edit form
     * @param model
     *            lemma model that is edited
     */
    public LemmaViewForm(String id, IModel<Lemma> model) {
        super(id, model);

        TextField<String> nameTextField = new TextField<String>("name");
        LemmaTextField replacementTextField = new LemmaTextField("replacement");
        TextField posTextField = new TextField("posString");
        ListChoice<Source.LemmaType> sourceListChoice = new ListChoice<Source.LemmaType>("source",
                new PropertyModel<Source.LemmaType>(getModelObject(), "source"),
                new ArrayList<Source.LemmaType>(Arrays.asList(Source.LemmaType.values())),
                new EnumChoiceRenderer<Source.LemmaType>(), 1);
        TextField referenceTextField = new TextField("reference");

        add(nameTextField.setEnabled(false));
        add(replacementTextField.setEnabled(false));
        add(posTextField.setEnabled(false));
        add(sourceListChoice.setEnabled(false));
        add(referenceTextField.setEnabled(false));
        add(new ToIndexButton("toIndexButton"));
    }

    /**
     * A button which redirects to the lemma index page.
     */
    private final class ToIndexButton extends AjaxLink<Lemma> {
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
        public ToIndexButton(String id) {
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
            setResponsePage(LemmaIndexPage.class);
        }
    }
}
