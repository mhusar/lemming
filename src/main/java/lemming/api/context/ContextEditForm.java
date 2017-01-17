package lemming.api.context;

import lemming.api.lemma.LemmaAutoCompleteTextField;
import lemming.api.lemma.LemmaTextField;
import lemming.api.pos.PosAutoCompleteTextField;
import lemming.api.pos.PosTextField;
import lemming.api.ui.NonTrimmingTextField;
import lemming.api.ui.panel.ModalMessagePanel;
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
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Class of the next page.
     */
    private Class<? extends Page> nextPageClass;

    /**
     * Creates a new context edit form.
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
        RequiredTextField<String> keywordTextField = new RequiredTextField<String>("keyword");
        TextField<String> followingTextField = new NonTrimmingTextField("following");

        add(lemmaTextField);
        add(posTextField);
        add(typeListChoice);
        add(locationTextField);
        add(precedingTextField.setRequired(true));
        add(keywordTextField);
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
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a new cancel button.
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
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a new delete button.
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
            ModalMessagePanel contextDeleteDeniedPanel = (ModalMessagePanel) getPage().get("contextDeleteDeniedPanel");

            // TODO:
            //if (new ContextDao().getFicheCount(getModelObject()) > 0) {
            //    contextDeleteDeniedPanel.show(target, getModel());
            //} else {
                contextDeleteConfirmPanel.show(target, getModel());
            //}
        }
    }
}
