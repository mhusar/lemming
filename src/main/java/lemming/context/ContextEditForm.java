package lemming.context;

import lemming.lemma.LemmaAutoCompleteTextField;
import lemming.lemma.LemmaTextField;
import lemming.pos.PosAutoCompleteTextField;
import lemming.pos.PosTextField;
import lemming.ui.NonTrimmingTextField;
import lemming.ui.panel.ModalMessagePanel;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A form for editing contexts.
 */
public class ContextEditForm extends Form<Context> {
    /**
     * Class of the next page.
     */
    private Class<? extends Page> nextPageClass;

    /**
     * Creates a context edit form.
     *
     * @param id
     *            ID of the edit form
     * @param model
     *            context model that is edited
     * @param nextPageClass
     *            class of the next page
     */
    public ContextEditForm(String id, IModel<Context> model, Class<? extends Page> nextPageClass) {
        super(id, model);

        this.nextPageClass = nextPageClass;
        LemmaTextField lemmaTextField = new LemmaAutoCompleteTextField("lemma");
        PosTextField posTextField = new PosAutoCompleteTextField("pos");
        ListChoice<ContextType.Type> typeListChoice = new ListChoice<ContextType.Type>("type",
                new PropertyModel<ContextType.Type>(getModelObject(), "type"),
                new ArrayList<ContextType.Type>(Arrays.asList(ContextType.Type.values())),
                new EnumChoiceRenderer<ContextType.Type>(), 1);
        RequiredTextField<String> locationTextField = new RequiredTextField<String>("location");
        TextField<String> precedingTextField = new NonTrimmingTextField("preceding");
        TextField<String> initPunctuationTextField = new NonTrimmingTextField("initPunctuation");
        RequiredTextField<String> keywordTextField = new RequiredTextField<String>("keyword");
        TextField<String> endPunctuationTextField = new NonTrimmingTextField("endPunctuation");
        TextField<String> followingTextField = new NonTrimmingTextField("following");

        add(lemmaTextField);
        add(posTextField);
        add(typeListChoice);
        add(locationTextField);
        add(precedingTextField.setRequired(true));
        add(initPunctuationTextField);
        add(keywordTextField);
        add(endPunctuationTextField);
        add(followingTextField.setRequired(true));
        add(new CancelButton("cancelButton"));
        add(new DeleteButton("deleteButton", model).setVisible(!(isContextTransient(model))));
    }

    /**
     * Checks if a context model is transient.
     *
     * @param model
     *            context model that is checked
     * @return True if a context model is transient; false otherwise.
     */
    private Boolean isContextTransient(IModel<Context> model) {
        return new ContextDao().isTransient(model.getObject());
    }

    /**
     * Called on form submit.
     */
    @Override
    protected void onSubmit() {
        ContextDao contextDao = new ContextDao();
        Context context = getModelObject();

        if (contextDao.isTransient(context)) {
            contextDao.persist(context);
        } else {
            contextDao.merge(context);
        }

        if (nextPageClass instanceof Class) {
            setResponsePage(nextPageClass);
        } else {
            setResponsePage(ContextEditPage.class);
        }
    }

    /**
     * A button which cancels the editing of a context.
     */
    private final class CancelButton extends AjaxLink<Context> {
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
                setResponsePage(ContextIndexPage.class);
            }
        }
    }

    /**
     * A button which deletes a context.
     */
    private final class DeleteButton extends AjaxLink<Context> {
        /**
         * Creates a delete button.
         *
         * @param id
         *            ID of the button
         * @param model
         *            model which is deleted by the button
         */
        private DeleteButton(String id, IModel<Context> model) {
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
            ModalMessagePanel contextDeleteConfirmPanel = (ModalMessagePanel) getPage()
                    .get("contextDeleteConfirmPanel");
            // not needed
            ModalMessagePanel contextDeleteDeniedPanel = (ModalMessagePanel) getPage().get("contextDeleteDeniedPanel");

            contextDeleteConfirmPanel.show(target, getModel());
        }
    }
}
