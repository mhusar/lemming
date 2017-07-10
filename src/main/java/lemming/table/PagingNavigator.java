package lemming.table;

import java.io.Serializable;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigation;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigationIncrementLink;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigationLink;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigation;
import org.apache.wicket.model.IModel;

/**
 * An extended AjaxPagingNavigator with custom markup and style.
 */
class PagingNavigator extends AjaxPagingNavigator {
    /**
     * Fixed view size.
     */
    private static final int VIEW_SIZE = 5;

    /**
     * Creates a paging navigator.
     * 
     * @param id
     *            ID of this navigator
     * @param pageable
     *            data table or data view that is paged
     */
    public PagingNavigator(String id, IPageable pageable) {
        super(id, pageable);
    }

    /**
     * Creates a paging navigation with numbered page links.
     *
     * @param id
     *            ID of this navigator
     * @param pageable
     *            data table or data view that is paged
     * @param labelProvider
     *            provider for the label of this navigator
     * @return A new paging navigation.
     */
    @Override
    protected PagingNavigation newNavigation(String id, IPageable pageable, IPagingLabelProvider labelProvider) {
        PagingNavigation navigation = new AjaxPagingNavigation(id, pageable, labelProvider) {
            /**
             * @param iteration iteration index
             * @return A loop item.
             */
            @Override
            protected LoopItem newItem(int iteration) {
                LoopItem item = super.newItem(iteration);

                long pageIndex = getStartIndex() + iteration;
                item.add(new AttributeModifier("class", new PagingLinkModel(pageable, pageIndex, "active")));
                return item;
            }
        };

        navigation.setViewSize(VIEW_SIZE);
        return navigation;
    }

    /**
     * Creates a page link for decrement/increment items.
     *
     * @param id
     *            ID of the link
     * @param pageable
     *            data table or data view that is paged
     * @param increment
     *            the amount of incrementation
     * @return A page link.
     */
    @Override
    protected AbstractLink newPagingNavigationIncrementLink(String id, IPageable pageable, int increment) {
        ExternalLink container = new ExternalLink(id + "Item", (String) null);

        container.add(new AttributeModifier("class", new PagingLinkIncrementModel(pageable,
                pageable.getCurrentPage() + increment)));
        container.add(new AjaxPagingNavigationIncrementLink(id, pageable, increment) {
            /**
             * Always generate a href attribute.
             *
             * @param tag
             *            The component tag.
             */
            @Override
            protected void disableLink(ComponentTag tag) {
            }
        });

        return container;
    }

    /**
     * Creates a page link for first/last page items.
     *
     * @param id
     *            ID of the link
     * @param pageable
     *            data table or data view that is paged
     * @param pageNumber
     *            page to jump to
     * @return A page link.
     */
    protected AbstractLink newPagingNavigationLink(String id, IPageable pageable, int pageNumber) {
        ExternalLink container = new ExternalLink(id + "Item", (String) null);

        container.add(new AttributeModifier("class",
                new PagingLinkModel(pageable, pageable.getCurrentPage() + pageNumber, "disabled")));
        container.add(new AjaxPagingNavigationLink(id, pageable, pageNumber) {
            /**
             * Always generate a href attribute.
             *
             * @param tag
             *            The component tag.
             */
            @Override
            protected void disableLink(ComponentTag tag) {
            }
        });

        return container;
    }

    /**
     * A model representing label and style of a paging link.
     */
    private class PagingLinkModel implements IModel<String>, Serializable {
        /**
         * Page number of a link.
         */
        private final long pageNumber;

        /**
         * Data table or data view to be paged.
         */
        protected final IPageable pageable;

        /**
         * The CSS value used to style a link.
         */
        private final String cssValue;

        /**
         * Creates a link model.
         *
         * @param pageable
         *            data table or data view that is paged
         * @param pageNumber
         *            page number of a link
         * @param cssValue
         *            CSS value used to style a link
         */
        public PagingLinkModel(IPageable pageable, long pageNumber, String cssValue) {
            this.pageNumber = pageNumber;
            this.pageable = pageable;
            this.cssValue = cssValue;
        }

        /**
         * Returns the CSS value.
         *
         * @return A string representing the CSS value.
         */
        @Override
        public String getObject() {
            return isSelected() ? cssValue : "";
        }

        /**
         * Does nothing.
         *
         * @param object
         *            Some object.
         */
        @Override
        public void setObject(String object) {
        }

        /**
         * Does nothing.
         */
        @Override
        public void detach() {
        }

        /**
         * Determines if the linked page is selected.
         *
         * @return True if the linked page is selected; false otherwise.
         */
        public boolean isSelected() {
            return getPageNumber() == pageable.getCurrentPage();
        }

        /**
         * Returns the page number of the linked page.
         *
         * @return The number of the linked page.
         */
        private long getPageNumber() {
            long pageNumber = this.pageNumber;

            if (pageNumber < 0) {
                pageNumber = pageable.getPageCount() + pageNumber;
            }

            if (pageNumber > (pageable.getPageCount() - 1)) {
                pageNumber = pageable.getPageCount() - 1;
            }

            if (pageNumber < 0) {
                pageNumber = 0;
            }

            return pageNumber;
        }
    }

    /**
     * A model representing label and style of a increment paging link.
     */
    private class PagingLinkIncrementModel implements IModel<String>, Serializable {
        /**
         * Data table or data view to be paged.
         */
        protected final IPageable pageable;

        /**
         * Page number of a link.
         */
        private final long pageNumber;

        /**
         * Creates a link model.
         *
         * @param pageable
         *            data table or data view that is paged
         * @param pageNumber
         *            page number of a link
         */
        public PagingLinkIncrementModel(IPageable pageable, long pageNumber) {
            this.pageable = pageable;
            this.pageNumber = pageNumber;
        }

        /**
         * Returns the CSS value.
         *
         * @return A string representing the CSS value.
         */
        @Override
        public String getObject() {
            return isEnabled() ? "" : "disabled";
        }

        /**
         * Does nothing.
         *
         * @param object
         *            Some object.
         */
        @Override
        public void setObject(String object) {
        }

        /**
         * Does nothing.
         */
        @Override
        public void detach() {
        }

        /**
         * Determines if the linked page is enabled.
         *
         * @return True if the linked page is enabled; false otherwise.
         */
        public boolean isEnabled() {
            if (pageNumber < 0) {
                return !isFirstPage();
            } else {
                return !isLastPage();
            }
        }

        /**
         * Determines if the linked page is the first page.
         *
         * @return True if the linked page if the first page; false otherwise.
         */
        public boolean isFirstPage() {
            return pageable.getCurrentPage() <= 0;
        }

        /**
         * Determines if the linked page is the last page.
         *
         * @return True if the linked page if the last page; false otherwise.
         */
        public boolean isLastPage() {
            return pageable.getCurrentPage() >= (pageable.getPageCount() - 1);
        }
    }
}
