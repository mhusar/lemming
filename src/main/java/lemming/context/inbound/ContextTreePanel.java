package lemming.context.inbound;

import lemming.context.BaseContext;
import lemming.context.Context;
import lemming.ui.panel.AlertPanel;
import lemming.ui.panel.ModalMessagePanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

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
        DataView<InboundContext> dataView = new InboundContextDataView(inboundContexts, tree);

        tree.add(AttributeModifier.append("class", "tree tree-theme-windows"));
        tree.setOutputMarkupId(true);
        listContainer.add(dataView);
        listContainer.setOutputMarkupId(true);

        add(new Label("heading", location));
        add(new CollapseAllButton(tree));
        add(new ExpandAllButton(tree));
        add(new ApplyButton(tree));
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
     * A button applying data of a tree.
     */
    private class ApplyButton extends AjaxLink<Void> {
        /**
         * The associated tree.
         */
        private ContextTree tree;

        /**
         * Creates an apply button.
         *
         * @param tree associated tree
         */
        public ApplyButton(ContextTree tree) {
            super("applyButton");
            this.tree = tree;
        }

        /**
         * Called on click.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            ContextTreeProvider provider = (ContextTreeProvider) tree.getProvider();
            final Iterator<? extends BaseContext> rootIterator = provider.getRoots();
            final InboundContextDao inboundContextDao = new InboundContextDao();

            while (rootIterator.hasNext()) {
                Context root = (Context) rootIterator.next();

                if (provider.hasChildren(root)) {
                    final Iterator<? extends BaseContext> childrenIterator = provider.getChildren(root);

                    while (childrenIterator.hasNext()) {
                        InboundContext child = (InboundContext) childrenIterator.next();
                        child = inboundContextDao.refresh(child);
                        child.setMatch(root);
                        inboundContextDao.merge(child);
                    }
                }
            }

            target.add(tree);
        }
    }

    /**
     * A list view for inbound contexts.
     */
    private class InboundContextDataView extends DataView<InboundContext> {
        private ContextTree tree;

        /**
         * Creates a data view.
         *
         * @param contexts inbound contexts
         */
        public InboundContextDataView(List<InboundContext> contexts, ContextTree tree) {
            super("inboundContextDataView", new InboundContextDataProvider(contexts));
            this.tree = tree;
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
            item.add(new AddButton(this, tree, item.getModel()));
        }
    }

    /**
     * A DataProvider for inbound contexts.
     */
    public class InboundContextDataProvider implements IDataProvider<InboundContext> {

        /**
         * A list of contexts.
         */
        private List<InboundContext> contexts;

        /**
         * Creates an InboundContextDataProvider.
         *
         * @param contexts a list of contexts
         */
        InboundContextDataProvider(List<InboundContext> contexts) {
            contexts.sort((context1, context2) -> context1.getNumber().compareTo(context2.getNumber()));
            this.contexts = contexts;
        }

        /**
         * Adds a context
         *
         * @param context a context
         */
        public void add(InboundContext context) {
            contexts.add(context);
            contexts.sort((context1, context2) -> context1.getNumber().compareTo(context2.getNumber()));
        }

        /**
         * Removes a context.
         *
         * @param context a context
         */
        public void remove(InboundContext context) {
            contexts.remove(context);
        }

        /**
         * Checks if the provider contains a context.
         *
         * @param context a context.
         * @return True or false.
         */
        public boolean contains(InboundContext context) {
            return contexts.contains(context);
        }

        /**
         * Returns an iterator over a list of contexts.
         *
         * @param first index of first context
         * @param count number of contexts
         * @return An iterator.
         */
        @Override
        public Iterator<? extends InboundContext> iterator(long first, long count) {
            int fromIndex = (int) first;
            int toIndex = (int) (first + count);
            return contexts.subList(fromIndex, toIndex).iterator();
        }

        /**
         * Returns the number of contexts.
         *
         * @return A number.
         */
        @Override
        public long size() {
            return contexts.size();
        }

        /**
         * Wraps a context into a model.
         *
         * @param context a context
         * @return Model of a context.
         */
        @Override
        public IModel<InboundContext> model(InboundContext context) {
            return Model.of(context);
        }

        /**
         * Does nothing.
         */
        @Override
        public void detach() {
        }
    }

    /**
     * A button that adds contexts from a DataView to a Tree.
     */
    private class AddButton extends AjaxLink<InboundContext> {

        /**
         * A DataView.
         */
        private InboundContextDataView dataView;

        /**
         * A Tree.
         */
        private ContextTree tree;

        /**
         * Creates an AddButton.
         *
         * @param dataView a DataView
         * @param tree a Tree
         * @param model model of a context
         */
        public AddButton(InboundContextDataView dataView, ContextTree tree, IModel<InboundContext> model) {
            super("addButton", model);
            add(new Label("icon"));
            this.dataView = dataView;
            this.tree = tree;
        }

        /**
         * Called on click.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            InboundContextDataProvider dataProvider = (InboundContextDataProvider) dataView.getDataProvider();
            ContextTreeProvider treeProvider = (ContextTreeProvider) tree.getProvider();
            BaseContext selectedContext = tree.getSelectedContext();
            ModalMessagePanel warningPanel = (ModalMessagePanel) getPage().get("contextAddWarningPanel");

            if (selectedContext instanceof Context) {
                if (treeProvider.hasChildren(selectedContext)) {
                    warningPanel.show(target, getModel());
                } else {
                    dataProvider.remove(getModelObject());
                    treeProvider.add((Context) tree.getSelectedContext(), getModelObject());
                    target.add(dataView.getParent());
                    target.add(tree);
                }
            }
        }
    }
}
