package lemming.context.inbound;

import lemming.context.BaseContext;
import lemming.context.Context;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * A folder component for contexts.
 */
class ContextFolder extends Folder<BaseContext> {
    private ContextTree tree;

    /**
     * Creates a folder.
     *
     * @param id if of the folder
     * @param tree parent tree
     * @param model folder model
     */
    ContextFolder(String id, ContextTree tree, DataView<InboundContext> dataView, IModel<BaseContext> model) {
        super(id, tree, model);
        this.tree = tree;

        AjaxLink<BaseContext> addButton = new AddButton(tree, model);
        AjaxLink<BaseContext> removeButton = new RemoveButton(tree, dataView, model);

        add(addButton);
        add(removeButton);

        if (getModelObject() instanceof Context) {
            addButton.setEnabled(false).setVisible(false);
            removeButton.setEnabled(false).setVisible(false);
        }
    }

    /**
     * Creates a label model.
     *
     * @param model folder model
     * @return The label model.
     */
    @Override
    protected IModel<?> newLabelModel(IModel<BaseContext> model) {
        BaseContext context = model.getObject();
        return Model.of(String.format("<b>%d</b>: %s", context.getNumber(), context.getKeyword()));
    }

    /**
     * Creates a label component.
     *
     * @param id id of the component
     * @param model model of the component
     * @return The component.
     */
    @Override
    protected Component newLabelComponent(String id, IModel<BaseContext> model) {
        Component component = super.newLabelComponent(id, model);
        component.setEscapeModelStrings(false);
        return component;
    }

    /**
     * Creates a link component.
     *
     * @param id id of the component
     * @param model model of the component
     * @return The component.
     */
    @Override
    protected MarkupContainer newLinkComponent(String id, IModel<BaseContext> model) {
        MarkupContainer container = super.newLinkComponent(id, model);

        container.setEscapeModelStrings(false);
        return container;
    }

    /**
     * Checks if a folder is clickable.
     *
     * @return True or false.
     */
    @Override
    protected boolean isClickable() {
        return getModelObject() instanceof Context;
    }

    /**
     * Called on click.
     *
     * @param target target that produces an Ajax response
     */
    @Override
    protected void onClick(AjaxRequestTarget target) {
        super.onClick(target);
        tree.expand(getModelObject());
        tree.setSelectedContext(getModelObject());
        target.add(tree);
    }

    /**
     * Checks if the model of the folder is selected.
     *
     * @return True or false.
     */
    @Override
    protected boolean isSelected() {
        return getModelObject().equals(tree.getSelectedContext());
    }

    /**
     * Returns a style class for the folder.
     *
     * @return A style class.
     */
    @Override
    protected String getStyleClass() {
        String styleClass = super.getStyleClass();
        ITreeProvider<BaseContext> provider = tree.getProvider();
        StringBuilder stringBuilder = new StringBuilder().append(styleClass);
        InboundContextDao inboundContextDao = new InboundContextDao();

        if (getModelObject() instanceof InboundContext) {
            InboundContext child = (InboundContext) getModelObject();
            child = inboundContextDao.refresh(child);

            if (child.getMatch() != null) {
                stringBuilder.append(" has-match");
            }

            return stringBuilder.toString();
        }

        if (provider.hasChildren(getModelObject())) {
            List<BaseContext> children = new ArrayList<>();
            provider.getChildren(getModelObject()).forEachRemaining(children::add);

            if (children.size() > 1) {
                return stringBuilder.append(" has-multiple-children").toString();
            } else {
                InboundContext child = (InboundContext) children.get(0);
                child = inboundContextDao.refresh(child);

                if (child.getMatch() != null) {
                    stringBuilder.append(" has-match");
                }

                if (getModelObject().getKeyword().equals(children.get(0).getKeyword())) {
                    return stringBuilder.append(" has-equal-child").toString();
                } else {
                    return stringBuilder.append(" has-different-child").toString();
                }
            }
        } else {
            return stringBuilder.append(" has-no-children").toString();
        }
    }

    /**
     * A button which adds a context to a different folder.
     */
    private class AddButton extends AjaxLink<BaseContext> {

        /**
         * A ContextTree.
         */
        private ContextTree tree;

        /**
         * Creates an AddButton.
         *
         * @param tree context tree
         * @param model model of a context
         */
        public AddButton(ContextTree tree, IModel<BaseContext> model) {
            super("addButton", model);
            this.tree = tree;
            add(new Label("icon"));
        }

        /**
         * Called on click.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            ContextTreeProvider provider = (ContextTreeProvider) tree.getProvider();

            if (ContextFolder.this.getModelObject() instanceof InboundContext) {
                Context selectedContext = (Context) tree.getSelectedContext();
                InboundContext modelObject = (InboundContext) ContextFolder.this.getModelObject();

                if (selectedContext != null) {
                    provider.remove(modelObject);
                    provider.add(selectedContext, modelObject);
                    target.add(tree);
                }
            }
        }
    }

    /**
     * A button which removes a context from a folder.
     */
    private class RemoveButton extends AjaxLink<BaseContext> {
        /**
         * A ContextTree.
         */
        private ContextTree tree;

        /**
         * A DataView for contexts.
         */
        DataView<InboundContext> dataView;

        /**
         * Creates a RemoveButton.
         *
         * @param tree context  tree
         * @param dataView dataView for contexts
         * @param model model of a context
         */
        public RemoveButton(ContextTree tree, DataView<InboundContext> dataView, IModel<BaseContext> model) {
            super("removeButton", model);
            this.tree = tree;
            this.dataView = dataView;
            add(new Label("icon"));
        }

        /**
         * Called on click.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            ContextTreeProvider provider = (ContextTreeProvider) tree.getProvider();

            if (ContextFolder.this.getModelObject() instanceof InboundContext) {
                provider.remove((InboundContext) ContextFolder.this.getModelObject());
                target.add(tree);

                for (Component component : tree.getParent()) {
                    if (component.getId().equals("listContainer")) {
                        WebMarkupContainer listContainer = (WebMarkupContainer) component;
                        IDataProvider dataProvider = dataView.getDataProvider();
                        ((ContextTreePanel.InboundContextDataProvider) dataProvider)
                                .add((InboundContext) ContextFolder.this.getModelObject());
                        target.add(listContainer);
                        break;
                    }
                }
            }
        }
    }
}
