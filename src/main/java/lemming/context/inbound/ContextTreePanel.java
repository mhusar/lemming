package lemming.context.inbound;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * A panel with a tree and lists used to map inbound contexts to contexts.
 */
public class ContextTreePanel extends Panel {
    /**
     * Creates a context tree panel.
     *
     * @param id of of the panel
     * @param location location of contexts
     * @param provider provider for contexts
     * @param inboundContexts list of unmatched inbound contexts
     */
    public ContextTreePanel(String id, String location, ContextTreeProvider provider,
                            List<InboundContext> inboundContexts) {
        super(id);
        ContextTree tree = new ContextTree(provider);

        tree.add(AttributeModifier.append("class", "tree tree-theme-windows"));
        tree.setOutputMarkupId(true);

        add(new Label("heading", location));
        add(new CollapseAllButton(tree));
        add(new ExpandAllButton(tree));
        add(tree);
        add(new InboundContextListView(inboundContexts));
    }

    /**
     * A button collapsing all folders of a tree.
     */
    private class CollapseAllButton extends AjaxLink<Void> {
        /**
         * The associated tree.
         */
        private ContextTree tree;

        /**
         * Creates a collapse all button.
         *
         * @param tree associated tree
         */
        public CollapseAllButton(ContextTree tree) {
            super("collapseAllButton");
            this.tree = tree;
        }

        /**
         * Called on click.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            tree.collapseAll();
            target.add(tree);
        }
    }

    /**
     * A button expanding all folders of a tree.
     */
    private class ExpandAllButton extends AjaxLink<Void> {
        /**
         * The associated tree.
         */
        private ContextTree tree;

        /**
         * Creates a expand all button.
         *
         * @param tree associated tree
         */
        public ExpandAllButton(ContextTree tree) {
            super("expandAllButton");
            this.tree = tree;
        }

        /**
         * Called on click.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            tree.expandAll();
            target.add(tree);
        }
    }

    /**
     * A list view for inbound contexts.
     */
    private class InboundContextListView extends ListView<InboundContext> {
        /**
         * Creates a list view.
         *
         * @param contexts inbound contexts
         */
        public InboundContextListView(List<InboundContext> contexts) {
            super("inboundContextListView", contexts);
        }

        /**
         * Creates an item.
         *
         * @param index index of item
         * @param itemModel model of item
         * @return The item.
         */
        @Override
        protected ListItem<InboundContext> newItem(int index, IModel<InboundContext> itemModel) {
            ListItem<InboundContext> item = super.newItem(index, itemModel);
            item.setOutputMarkupId(true);
            return item;
        }

        /**
         * Populates an item
         *
         * @param listItem the item
         */
        @Override
        protected void populateItem(ListItem<InboundContext> listItem) {
            InboundContext context = listItem.getModelObject();
            listItem.add(new Label("number", context.getNumber()));
            listItem.add(new Label("keyword", context.getKeyword()));
        }
    }
}
