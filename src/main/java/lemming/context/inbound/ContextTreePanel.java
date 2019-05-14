package lemming.context.inbound;

import lemming.context.Context;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import wicketdnd.*;
import wicketdnd.theme.WindowsTheme;

import javax.xml.crypto.Data;
import java.util.Comparator;
import java.util.Iterator;
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
        final WebMarkupContainer listDropTarget = new WebMarkupContainer("listDropTarget");
        DataView<InboundContext> dataView = new InboundContextDataView(inboundContexts);

        tree.add(AttributeModifier.append("class", "tree tree-theme-windows"));
        tree.setOutputMarkupId(true);
        tree.add(new WindowsTheme()); // from wicketdnd
        tree.add(new TreeDragSource(tree).drag(".tree-content.context-draggable"));
        tree.add(new TreeDropTarget(tree).dropCenter(".tree-content.context-droppable"));

        listContainer.add(new WindowsTheme()); // from wicketdnd
        listContainer.add(new ListDragSource(listContainer, dataView).drag("tbody tr"));
        listContainer.add(new ListDropTarget(listContainer, dataView).dropCenter("table"));
        listContainer.setOutputMarkupId(true);
        listContainer.add(listDropTarget);
        listDropTarget.setOutputMarkupId(true);
        listDropTarget.add(dataView);

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
    private class InboundContextDataView extends DataView<InboundContext> {
        /**
         * Creates a data view.
         *
         * @param contexts inbound contexts
         */
        public InboundContextDataView(List<InboundContext> contexts) {
            super("inboundContextDataView", new InboundContextDataProvider(contexts));
        }

        /**
         * Creates an item.
         *
         * @param id if of item
         * @param index index of item
         * @param model model of item
         * @return The item.
         */
        @Override
        protected Item<InboundContext> newItem(String id, int index, IModel<InboundContext> model) {
            Item<InboundContext> item =  super.newItem(id, index, model);
            item.setOutputMarkupId(true);
            return item;
        }

        /**
         * Populates an item
         *
         * @param item the item
         */
        @Override
        protected void populateItem(Item<InboundContext> item) {
            InboundContext context = item.getModelObject();
            item.add(new Label("number", context.getNumber()));
            item.add(new Label("keyword", context.getKeyword()));
        }
    }

    private class InboundContextDataProvider implements IDataProvider<InboundContext> {

        private List<InboundContext> contexts;

        InboundContextDataProvider(List<InboundContext> contexts) {
            contexts.sort((context1, context2) -> context1.getNumber().compareTo(context2.getNumber()));
            this.contexts = contexts;
        }

        public void add(InboundContext context) {
            contexts.add(context);
            contexts.sort((context1, context2) -> context1.getNumber().compareTo(context2.getNumber()));
        }

        public void remove(InboundContext context) {
            contexts.remove(context);
        }

        public boolean contains(InboundContext context) {
            return contexts.contains(context);
        }

        @Override
        public Iterator<? extends InboundContext> iterator(long first, long count) {
            int fromIndex = (int) first;
            int toIndex = (int) (first + count);
            return contexts.subList(fromIndex, toIndex).iterator();
        }

        @Override
        public long size() {
            return contexts.size();
        }

        @Override
        public IModel<InboundContext> model(InboundContext inboundContext) {
            return Model.of(inboundContext);
        }

        @Override
        public void detach() {
        }
    }

    private class ListDragSource extends DragSource {

        private WebMarkupContainer listContainer;

        private DataView<InboundContext> dataView;

        ListDragSource(WebMarkupContainer listContainer, DataView<InboundContext> dataView) {
            super(Operation.MOVE);
            this.dataView = dataView;
            this.listContainer = listContainer;
        }

        @Override
        public void onAfterDrop(AjaxRequestTarget target, Transfer transfer) {
            InboundContext inboundContext = transfer.getData();
            InboundContextDataProvider dataProvider = (InboundContextDataProvider) dataView.getDataProvider();
            dataProvider.remove(inboundContext);
            target.add(listContainer);
        }
    }

    private class ListDropTarget extends DropTarget {

        private WebMarkupContainer listContainer;

        private DataView<InboundContext> dataView;

        ListDropTarget(WebMarkupContainer listContainer, DataView<InboundContext> dataView) {
            super(Operation.MOVE);
            this.dataView = dataView;
            this.listContainer = listContainer;
        }

        @Override
        public void onDrop(AjaxRequestTarget target, Transfer transfer, Location location) throws Reject {
            InboundContext inboundContext = transfer.getData();
            InboundContextDataProvider dataProvider = (InboundContextDataProvider) dataView.getDataProvider();

            if (dataProvider.contains(inboundContext)) {
                throw new Reject();
            } else {
                dataProvider.add(inboundContext);
                target.add(listContainer);
            }
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
            inboundContext = provider.remove(inboundContext);
            transfer.setData(inboundContext);
        }

        @Override
        public void onAfterDrop(AjaxRequestTarget target, Transfer transfer) {
            target.add(tree);
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
