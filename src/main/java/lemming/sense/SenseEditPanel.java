package lemming.sense;

import lemming.lemma.Lemma;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

/**
 * A panel with a form to edit senses.
 */
public class SenseEditPanel extends Panel {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Model of the parent lemma.
     */
    private IModel<Lemma> lemmaModel;

    /**
     * Model of the edited sense object.
     */
    private IModel<Sense> senseModel;

    /**
     * Creates a sense edit panel.
     *
     * @param id ID of the panel
     * @param lemmaModel model of the parent lemma
     */
    public SenseEditPanel(String id, IModel<Lemma> lemmaModel) {
        super(id);
        this.lemmaModel = lemmaModel;
    }

    /**
     * Creates a sense edit panel.
     *
     * @param id ID of the panel
     * @param lemmaModel model of the parent lemma
     */
    public SenseEditPanel(String id, IModel<Lemma> lemmaModel, IModel<Sense> senseModel) {
        super(id);
        this.lemmaModel = lemmaModel;
        this.senseModel = senseModel;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new SenseEditForm("senseEditForm"));
    }

    /**
     * A form for editing senses.
     */
    private class SenseEditForm extends Form<Sense> {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a sense edit form.
         *
         * @param id ID of the edit form
         */
        public SenseEditForm(String id) {
            super(id);
            ITreeProvider<Sense> treeProvider = new SenseTreeProvider(lemmaModel.getObject());
            AbstractTree<Sense> senseTree;
            Sense sense;

            if (senseModel instanceof IModel) {
                sense = senseModel.getObject();
            } else {
                List<Sense> rootNodes = new SenseDao().findRootNodes(lemmaModel.getObject());

                if (rootNodes.isEmpty()) {
                    sense = new Sense();
                    sense.setLemma(lemmaModel.getObject());
                } else {
                    sense = rootNodes.get(0);
                }
            }

            add(new SenseTree("senses", treeProvider, sense));
            add(new TextField<String>("meaning", new PropertyModel<String>(sense, "meaning")));
            add(new CreateSenseButton("createSenseButton"));
            add(new DeleteSenseButton("deleteSenseButton"));
            add(new SaveSenseButton("saveSenseButton"));
        }

        /**
         * A button which creates a sense.
         */
        private class CreateSenseButton extends AjaxLink<Void> {
            /**
             * Create a create sense button
             *
             * @param id ID of the button
             */
            public CreateSenseButton(String id) {
                super(id);
            }

            /**
             * Called on button click.
             *
             * @param target target that produces an Ajax response
             */
            @Override
            public void onClick(AjaxRequestTarget target) {

            }
        }

        /**
         * A button which deletes a sense.
         */
        private class DeleteSenseButton extends AjaxLink<Void> {
            /**
             * Creates a delete sense button.
             *
             * @param id ID of the button
             */
            public DeleteSenseButton(String id) {
                super(id);
            }

            /**
             * Called on button click.
             *
             * @param target target that produces an Ajax response
             */
            @Override
            public void onClick(AjaxRequestTarget target) {

            }
        }

        /**
         * A button which saves a sense.
         */
        private class SaveSenseButton extends AjaxLink<Void> {
            /**
             * Creates a save sense button.
             *
             * @param id ID of the button
             */
            public SaveSenseButton(String id) {
                super(id);
            }

            /**
             * Called on button click.
             *
             * @param target target that produces an Ajax response
             */
            @Override
            public void onClick(AjaxRequestTarget target) {

            }
        }
    }
}
