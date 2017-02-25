package lemming.sense;

import lemming.lemma.Lemma;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
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
     * Type of a sense.
     */
    public enum SenseType {
        PARENT, CHILD
    }

    /**
     * Creates a sense edit panel.
     *
     * @param id ID of the panel
     * @param lemmaModel model of a parent lemma
     */
    public SenseEditPanel(String id, IModel<Lemma> lemmaModel) {
        super(id);
        add(new SenseEditForm("senseEditForm", lemmaModel));
    }

    /**
     * Creates a sense edit panel.
     *
     * @param id ID of the panel
     * @param lemmaModel model of a parent lemma
     * @param senseModel model of a sense
     */
    public SenseEditPanel(String id, IModel<Lemma> lemmaModel, IModel<Sense> senseModel) {
        super(id);
        add(new SenseEditForm("senseEditForm", lemmaModel, senseModel));
    }

    /**
     * A form for editing senses.
     */
    private class SenseEditForm extends Form<Sense> implements SenseTree.SelectListener {
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
         * Parent sense model of the edited sense object.
         */
        private IModel<Sense> parentSenseModel;

        /**
         * Sense type which is saved.
         */
        private SenseType type;

        /**
         * A button which adds a sense.
         */
        private AddSenseButton addSenseButton;

        /**
         * A button which adds a child sense.
         */
        private AddChildSenseButton addChildSenseButton;

        /**
         * A button which deletes a sense.
         */
        private DeleteSenseButton deleteSenseButton;

        /**
         * A button which saves a sense.
         */
        private SaveSenseButton saveSenseButton;

        /**
         * A tree of senses and child senses.
         */
        private SenseTree senseTree;

        /**
         * A text field displaying the meaning of a sense.
         */
        private TextField<String> meaningTextField;

        /**
         * Creates a sense edit form.
         *
         * @param id ID of the panel
         * @param lemmaModel model of a parent lemma
         */
        public SenseEditForm(String id, IModel<Lemma> lemmaModel) {
            super(id);
            this.lemmaModel = lemmaModel;
        }

        /**
         * Creates a sense edit form.
         *
         * @param id ID of the panel
         * @param lemmaModel model of a parent lemma
         * @param senseModel model of a sense
         */
        public SenseEditForm(String id, IModel<Lemma> lemmaModel, IModel<Sense> senseModel) {
            super(id);
            this.lemmaModel = lemmaModel;
            this.senseModel = senseModel;
            parentSenseModel = new Model<Sense>();
        }

        /**
         * Called when a sense edit form is initialized.
         */
        @Override
        protected void onInitialize() {
            super.onInitialize();
            SenseDao senseDao = new SenseDao();
            List<Sense> rootNodes = senseDao.findRootNodes(lemmaModel.getObject());
            ITreeProvider<Sense> treeProvider = new SenseTreeProvider(lemmaModel.getObject());

            if (!(senseModel instanceof IModel) && !rootNodes.isEmpty()) {
                Sense firstRootNode = rootNodes.get(0);
                senseModel = new Model<Sense>(firstRootNode);
            }

            if (senseModel instanceof IModel) {
                senseTree = new SenseTree("senses", treeProvider, new Model<Sense>(senseModel.getObject()));
                meaningTextField = new TextField<String>("meaning", new PropertyModel<String>(senseModel, "meaning"));
            } else {
                senseTree = new SenseTree("senses", treeProvider);
                meaningTextField = new TextField<String>("meaning", new Model<String>(""));
            }

            senseTree.expandAll();
            senseTree.registerSelectListener(this);
            SenseEditPanel.this.add(new SenseDeleteConfirmPanel("senseDeleteConfirmPanel", lemmaModel, senseTree));

            addSenseButton = new AddSenseButton("addSenseButton");
            addChildSenseButton = new AddChildSenseButton("addChildSenseButton");
            deleteSenseButton = new DeleteSenseButton("deleteSenseButton");
            saveSenseButton = new SaveSenseButton("saveSenseButton", this);

            if (senseModel instanceof IModel) {
                if (senseModel.getObject().isParentSense()) {
                    type = SenseType.PARENT;

                    if (senseDao.hasChildSenses(senseModel.getObject())) {
                        deleteSenseButton.setEnabled(false);
                    }
                } else {
                    type = SenseType.CHILD;
                    addChildSenseButton.setVisible(false);
                }
            } else {
                meaningTextField.setEnabled(false);
                addChildSenseButton.setVisible(false);
                deleteSenseButton.setVisible(false);
                saveSenseButton.setVisible(false);
            }

            add(senseTree.setOutputMarkupPlaceholderTag(true).setOutputMarkupId(true));
            add(meaningTextField.setOutputMarkupId(true));
            add(addSenseButton.setOutputMarkupId(true));
            add(addChildSenseButton.setOutputMarkupPlaceholderTag(true).setOutputMarkupId(true));
            add(deleteSenseButton.setOutputMarkupPlaceholderTag(true).setOutputMarkupId(true));
            add(saveSenseButton.setOutputMarkupPlaceholderTag(true).setOutputMarkupId(true));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onSelect(AjaxRequestTarget target) {
            SenseDao senseDao = new SenseDao();
            IModel<Sense> selectedNodeModel = senseTree.getSelectedNodeModel();

            if (selectedNodeModel instanceof IModel) {
                senseModel = selectedNodeModel;

                if (senseModel.getObject() instanceof Sense) {
                    senseModel.setObject(senseDao.refresh(senseModel.getObject()));

                    if (senseModel.getObject().isParentSense()) {
                        setSenseType(SenseType.PARENT);
                        addChildSenseButton.setVisible(true).setEnabled(true);

                        if (senseDao.hasChildSenses(senseModel.getObject())) {
                            deleteSenseButton.setVisible(true).setEnabled(false);
                        } else {
                            deleteSenseButton.setVisible(true).setEnabled(true);
                        }

                        target.add(addChildSenseButton);
                        target.add(deleteSenseButton);
                    } else {
                        setSenseType(SenseType.CHILD);
                        addChildSenseButton.setVisible(true).setEnabled(false);
                        deleteSenseButton.setVisible(true).setEnabled(true);
                        target.add(addChildSenseButton);
                        target.add(deleteSenseButton);
                    }

                    meaningTextField.setDefaultModel(new PropertyModel<String>(senseModel, "meaning"));
                    target.add(meaningTextField);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onDeselect(AjaxRequestTarget target) {
            meaningTextField.setDefaultModel(new Model<String>(""));
            target.add(meaningTextField);
        }

        /**
         * Sets the sense type that is saved.
         *
         * @param type type of sense
         */
        private void setSenseType(SenseType type) {
            this.type = type;
        }

        /**
         * A button which adds a sense.
         */
        private class AddSenseButton extends AjaxLink<Void> {
            /**
             * Creates an add sense button.
             *
             * @param id ID of the button
             */
            public AddSenseButton(String id) {
                super(id);
            }

            /**
             * Called on button click.
             *
             * @param target target that produces an Ajax response
             */
            @Override
            public void onClick(AjaxRequestTarget target) {
                senseModel = new Model<Sense>(new Sense());

                setSenseType(SenseType.PARENT);
                meaningTextField.setDefaultModel(new PropertyModel<String>(senseModel, "meaning"));
                meaningTextField.setEnabled(true);
                saveSenseButton.setVisible(true);
                target.add(meaningTextField);
                target.add(saveSenseButton);
                target.focusComponent(meaningTextField);
                senseTree.deselect(target);
            }
        }

        /**
         * A button which adds a child sense.
         */
        private class AddChildSenseButton extends AjaxLink<Void> {
            /**
             * Creates an add child sense button.
             *
             * @param id ID of the button
             */
            public AddChildSenseButton(String id) {
                super(id);
            }

            /**
             * Called on button click.
             *
             * @param target target that produces an Ajax response
             */
            @Override
            public void onClick(AjaxRequestTarget target) {
                parentSenseModel.setObject(senseModel.getObject());
                senseModel = new Model<Sense>(new Sense());

                setSenseType(SenseType.CHILD);
                meaningTextField.setDefaultModel(new PropertyModel<String>(senseModel, "meaning"));
                meaningTextField.setEnabled(true);
                saveSenseButton.setVisible(true);
                target.add(meaningTextField);
                target.add(saveSenseButton);
                target.focusComponent(meaningTextField);
                senseTree.deselect(target);
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
                SenseDeleteConfirmPanel senseDeleteConfirmPanel = (SenseDeleteConfirmPanel) SenseEditPanel.this
                        .get("senseDeleteConfirmPanel");
                senseDeleteConfirmPanel.show(target, new Model<Sense>(senseModel.getObject()));
            }
        }

        /**
         * A button which saves a sense.
         */
        private class SaveSenseButton extends AjaxButton {
            /**
             * Creates a save sense button.
             *
             * @param id ID of the button
             */
            public SaveSenseButton(String id, Form<?> form) {
                super(id, form);
            }

            /**
             * Called on form submit.
             *
             * @param target target that produces an Ajax response
             * @param form form that is submitted
             */
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (type.equals(SenseType.PARENT)) {
                    saveSense(target, form);
                } else if (type.equals(SenseType.CHILD)) {
                    saveChildSense(target, form);
                }
            }
        }

        /**
         * Saves a sense.
         *
         * @param target target that produces an Ajax response
         * @param form form that is submitted
         */
        private void saveSense(AjaxRequestTarget target, Form<?> form) {
            SenseDao senseDao = new SenseDao();
            List<Sense> rootNodes = senseDao.findRootNodes(lemmaModel.getObject());
            String meaning = meaningTextField.getInput();
            Sense sense = senseModel.getObject();

            sense.setMeaning(meaning);

            if (new SenseDao().isTransient(sense)) {
                sense.setLemma(lemmaModel.getObject());

                if (!rootNodes.isEmpty()) {
                    Sense lastRootNode = rootNodes.get(rootNodes.size() - 1);
                    sense.setParentPosition(lastRootNode.getParentPosition() + 1);
                } else {
                    sense.setParentPosition(0);
                }

                senseDao.persist(sense);
                senseTree.select(target, senseDao.find(sense.getId()));
            } else {
                Sense mergedSense = senseDao.merge(sense);
                senseModel.setObject(mergedSense);
                senseTree.select(target, mergedSense);
            }
        }

        /**
         * Saves a child sense.
         *
         * @param target target that produces an Ajax response
         * @param form form that is submitted
         */
        private void saveChildSense(AjaxRequestTarget target, Form<?> form) {
            SenseDao senseDao = new SenseDao();
            String meaning = meaningTextField.getInput();
            Sense parentSense = parentSenseModel.getObject();
            List<Sense> children = parentSense.getChildren();
            Sense childSense = senseModel.getObject();

            childSense.setMeaning(meaning);

            if (senseDao.isTransient(childSense)) {
                childSense.setLemma(lemmaModel.getObject());
                childSense.setParentPosition(parentSense.getParentPosition());

                if (!children.isEmpty()) {
                    Sense lastChild = children.get(children.size() - 1);
                    childSense.setChildPosition(lastChild.getChildPosition() + 1);
                } else {
                    childSense.setChildPosition(0);
                }

                parentSense.getChildren().add(childSense);
                senseDao.persist(childSense);
                senseDao.merge(parentSense);
                senseTree.select(target, senseDao.find(childSense.getId()));
            } else {
                Sense mergedChildSense = senseDao.merge(childSense);
                senseModel.setObject(mergedChildSense);
                senseTree.select(target, mergedChildSense);
            }

            parentSenseModel.setObject(null);
        }
    }
}
