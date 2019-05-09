package lemming.context.inbound;

import lemming.context.Context;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import wicketdnd.*;
import wicketdnd.theme.WindowsTheme;

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
        final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
        ListView<InboundContext> listView = new InboundContextListView(inboundContexts);

        tree.add(AttributeModifier.append("class", "tree tree-theme-windows"));
        tree.setOutputMarkupId(true);
        tree.add(new WindowsTheme()); // from wicketdnd
        tree.add(new TreeDragSource(tree).drag(".tree-content.context-draggable"));
        tree.add(new TreeDropTarget(tree).dropCenter(".tree-content.context-droppable"));

        listContainer.add(new WindowsTheme()); // from wicketdnd
        listContainer.add(new ListDragSource(listContainer, listView).drag("tbody tr"));
        listContainer.setOutputMarkupId(true);
        listContainer.add(listView);

        add(new Label("heading", location));
        add(new CollapseAllButton(tree));
        add(new ExpandAllButton(tree));
        add(tree);
        add(listContainer);
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

    private class ListDragSource extends DragSource {

        private WebMarkupContainer listContainer;

        private ListView<InboundContext> listView;

        ListDragSource(WebMarkupContainer listContainer, ListView<InboundContext> listView) {
            super(Operation.MOVE);
            this.listView = listView;
            this.listContainer = listContainer;
        }

        @Override
        public void onAfterDrop(AjaxRequestTarget target, Transfer transfer) {
            InboundContext inboundContext = transfer.getData();
            listView.getModelObject().remove(inboundContext);
            target.add(listContainer);
        }
    }

    private class TreeDragSource extends DragSource {
        private ContextTree tree;

        TreeDragSource(ContextTree tree) {
            super(Operation.MOVE);
            this.tree = tree;
        }

        @Override
        public void onBeforeDrop(Component drag, Transfer transfer) throws Reject {
            InboundContext inboundContext = (InboundContext) drag.getDefaultModelObject();
            ContextTreeProvider provider = (ContextTreeProvider) tree.getProvider();
            provider.remove(inboundContext.getMatch(), inboundContext);
            transfer.setData(inboundContext);
        }
    }

    private class TreeDropTarget extends DropTarget {
        private ContextTree tree;

        public TreeDropTarget(ContextTree tree) {
            super(Operation.MOVE);
            this.tree = tree;
        }

        @Override
        public void onDrop(AjaxRequestTarget target, Transfer transfer, Location location) throws Reject {
            Context context = (Context) location.getComponent().getParent().getDefaultModelObject();
            InboundContext inboundContext = transfer.getData();

            ContextTreeProvider provider = (ContextTreeProvider) tree.getProvider();
            provider.add(context, inboundContext);
            target.add(tree);
        }
    }
}
