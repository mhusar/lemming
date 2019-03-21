package lemming.context.inbound;

import lemming.context.Context;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.tree.NestedTree;
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
     * @param id if of the panel
     * @param provider provider for contexts
     * @param contexts list of unmatched contexts
     * @param inboundContexts list of unmatched inbound contexts
     */
    public ContextTreePanel(String id, ContextTreeProvider provider, List<Context> contexts,
                            List<InboundContext> inboundContexts) {
        super(id);
        NestedTree tree = new ContextTree(provider);

        tree.add(AttributeModifier.append("class", "tree-theme-windows"));
        add(tree);
        add(new ContextListView(contexts));
        add(new InboundContextListView(inboundContexts));
    }

    /**
     * A list view for contexts.
     */
    private class ContextListView extends ListView<Context> {
        /**
         * Creates a list view.
         *
         * @param contexts contexts
         */
        public ContextListView(List<Context> contexts) {
            super("contextListView", contexts);
        }

        /**
         * Creates an item.
         *
         * @param index index of item
         * @param itemModel model of item
         * @return The item.
         */
        @Override
        protected ListItem<Context> newItem(int index, IModel<Context> itemModel) {
            ListItem<Context> item = super.newItem(index, itemModel);
            item.setOutputMarkupId(true);
            return item;
        }

        /**
         * Populates an item
         *
         * @param listItem the item
         */
        @Override
        protected void populateItem(ListItem<Context> listItem) {
            Context context = listItem.getModelObject();
            listItem.add(new Label("number", context.getNumber()));
            listItem.add(new Label("keyword", context.getKeyword()));
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
