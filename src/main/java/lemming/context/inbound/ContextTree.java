package lemming.context.inbound;

import lemming.context.BaseContext;
import lemming.context.Context;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.NestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A tree used to map inbound contexts to contexts.
 */
public class ContextTree extends NestedTree<BaseContext> {
    /**
     * Creates a tree.
     *
     * @param provider tree provider
     */
    public ContextTree(ITreeProvider<BaseContext> provider) {
        super("inboundContextTree", provider);
    }

    /**
     * Creates a content component.
     *
     * @param id if of the component
     * @param model model of the component
     * @return The component.
     */
    @Override
    protected Component newContentComponent(String id, IModel<BaseContext> model) {
        Component contextFolder = new ContextFolder(id, this, model);

        if (model.getObject() instanceof InboundContext) {
            contextFolder.add(AttributeModifier.append("class", "context-draggable"));
        } else if (model.getObject() instanceof Context) {
            contextFolder.add(AttributeModifier.append("class", "context-droppable"));
        }

        contextFolder.setOutputMarkupId(true);
        return contextFolder;
    }

    /**
     * Collapses all roots.
     */
    public void collapseAll() {
        Iterator<? extends BaseContext> iterator = getProvider().getRoots();

        while (iterator.hasNext()) {
            collapse(iterator.next());
        }
    }

    /**
     * Expands all roots.
     */
    public void expandAll() {
        Iterator<? extends BaseContext> iterator = getProvider().getRoots();

        while (iterator.hasNext()) {
            expand(iterator.next());
        }
    }

    /**
     * A folder component for contexts.
     */
    private class ContextFolder extends Folder<BaseContext> {
        /**
         * Creates a folder.
         *
         * @param id if of the folder
         * @param tree parent tree
         * @param model folder model
         */
        ContextFolder(String id, AbstractTree<BaseContext> tree, IModel<BaseContext> model) {
            super(id, tree, model);
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
         * Returns a style class for the folder.
         *
         * @return A style class.
         */
        @Override
        protected String getStyleClass() {
            String styleClass = super.getStyleClass();
            ITreeProvider<BaseContext> provider = ContextTree.this.getProvider();
            StringBuilder stringBuilder = new StringBuilder().append(styleClass);

            if (getModelObject() instanceof InboundContext) {
                return stringBuilder.toString();
            }

            if (provider.hasChildren(getModelObject())) {
                List<BaseContext> children = new ArrayList<>();
                provider.getChildren(getModelObject()).forEachRemaining(children::add);

                if (children.size() > 1) {
                    return stringBuilder.append(" has-multiple-children").toString();
                } else if (getModelObject().getKeyword().equals(children.get(0).getKeyword())) {
                    return stringBuilder.toString();
                } else {
                    return stringBuilder.append(" has-different-child").toString();
                }
            } else {
                return stringBuilder.append(" has-no-children").toString();
            }
        }
    }
}
