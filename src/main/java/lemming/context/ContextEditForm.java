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
     * @param model context model that is edited
     * @param nextPageClass class of the next page
     */
    public ContextEditForm(IModel<Context> model, Class<? extends Page> nextPageClass) {
        super("contextEditForm", model);

        this.nextPageClass = nextPageClass;
        LemmaTextField lemmaTextField = new LemmaAutoCompleteTextField("lemma");
        PosTextField posTextField = new PosAutoCompleteTextField("pos");
        ListChoice<ContextType.Type> typeListChoice = new ListChoice<>("type",
                new PropertyModel<>(getModelObject(), "type"),
                new ArrayList<>(Arrays.asList(ContextType.Type.values())),
                new EnumChoiceRenderer<>(), 1);
        RequiredTextField<String> locationTextField = new RequiredTextField<>("location");
        TextField<String> precedingTextField = new NonTrimmingTextField("preceding");
        RequiredTextField<String> keywordTextField = new RequiredTextField<>("keyword");
        TextField<String> followingTextField = new NonTrimmingTextField("following");

        add(lemmaTextField);
        add(posTextField);
        add(typeListChoice);
        add(locationTextField);
        add(precedingTextField.setRequired(true));
        add(keywordTextField);
        add(followingTextField.setRequired(true));
        add(new CancelButton());
        add(new DeleteButton(model).setVisible(!(isContextTransient(model))));
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

        if (nextPageClass != null) {
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
         */
        public CancelButton() {
            super("cancelButton");
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
         * @param model model which is deleted by the button
         */
        private DeleteButton(IModel<Context> model) {
            super("deleteButton", model);
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
            @SuppressWarnings("unused")
            ModalMessagePanel contextDeleteDeniedPanel = (ModalMessagePanel) getPage().get("contextDeleteDeniedPanel");
            contextDeleteConfirmPanel.show(target, getModel());
        }
    }
}
