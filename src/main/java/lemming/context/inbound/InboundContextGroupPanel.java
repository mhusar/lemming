package lemming.context.inbound;

import lemming.context.ContextVerificationPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A panel which lists groups of inbound contexts.
 */
public class InboundContextGroupPanel extends Panel {
    /**
     * A placeholder that is displayed when the context group view is empty.
     */
    private final MarkupContainer placeholder;

    /**
     * A RefreshingView for entities of class InboundContextGroup.
     */
    private final InboundContextGroupView contextGroupView;

    /**
     * Creates an InboundContextGroupPanel.
     */
    public InboundContextGroupPanel() {
        super("contextGroupPanel");
        placeholder = new WebMarkupContainer("placeholder");
        contextGroupView = new InboundContextGroupView();
        add(placeholder);
        add(contextGroupView);
    }

    /**
     * Called when a InboundContextGroupPanel is configured.
     */
    @Override
    protected void onConfigure() {
        super.onConfigure();
        placeholder.setVisible(!contextGroupView.getItemModels().hasNext());
    }

    /**
     * A RefreshingView for entities of class InboundContextGroup.
     */
    private class InboundContextGroupView extends RefreshingView<InboundContextGroup> {
        /**
         * Create an InboundContextGroupView.
         */
        public InboundContextGroupView() {
            super("contextGroupView");
        }

        /**
         * Returns an iterator over models for items that will be added to this view.
         *
         * @return An iterator over models.
         */
        @Override
        protected Iterator<IModel<InboundContextGroup>> getItemModels() {
            List<IModel<InboundContextGroup>> itemModels = new ArrayList<>();

            for (InboundContextGroup contextGroup : new InboundContextGroupDao().getAll()) {
                itemModels.add(new Model<>(contextGroup));
            }

            return itemModels.iterator();
        }

        /**
         * Creates a location string based on begin and end location.
         *
         * @param contextGroupDao Data Access Object for inbound context groups
         * @param contextGroup    group of inbound contexts
         * @return A location string.
         */
        private String createLocationString(InboundContextGroupDao contextGroupDao, InboundContextGroup contextGroup) {
            String beginLocation = contextGroupDao.getBeginLocation(contextGroup);
            String endLocation = contextGroupDao.getEndLocation(contextGroup);
            String[] beginStrings = beginLocation.split("_"), endStrings = endLocation.split("_");
            String beginDocument = beginStrings[0], endDocument = endStrings[0],
                    beginRubric = beginStrings[1].replaceFirst("^0+", ""),
                    endRubric = endStrings[1].replaceFirst("^0+", "");

            if (beginDocument.equals(endDocument)) {
                return new StringResourceModel("InboundContextGroupView.location1", this)
                        .setParameters(beginDocument, beginRubric, endRubric).getString();
            } else {
                return new StringResourceModel("InboundContextGroupView.location2", this)
                        .setParameters(beginDocument, beginRubric, endDocument, endRubric).getString();
            }
        }

        /**
         * Populates the given item container.
         *
         * @param item item which is populated
         */
        @Override
        protected void populateItem(Item<InboundContextGroup> item) {
            InboundContextGroupDao contextGroupDao = new InboundContextGroupDao();
            InboundContextGroup contextGroup = item.getModel().getObject();
            Instant instant = contextGroup.getTimestamp().toInstant();
            String contextCount = String.valueOf(contextGroupDao.getContexts(contextGroup).size()),
                    location = createLocationString(contextGroupDao, contextGroup),
                    user = contextGroup.getUser().getRealName();
            String itemLabel = new StringResourceModel("InboundContextGroupView.itemLabel", this)
                    .setParameters(contextCount, location).getString();
            String popoverContent = new StringResourceModel("InboundContextGroupView.popoverContent", this)
                    .setParameters("<dl class='dl-horizontal' data-date='" + instant.toString() + "' data-locale='" +
                                    getString("InboundContextGroupView.popoverLocale") + "'><dt>",
                            "</dt><dd>" + user + "</dd><dt>", "</dt><dd class='date'></dd><dt>",
                            "</dt><dd class='time'></dd></dl>").getString();

            item.add(new Label("itemLabel", itemLabel));
            item.add(new WebMarkupContainer("detailsButton").add(AttributeModifier.append("data-html",
                    popoverContent)));
            item.add(new VerifyButton(item.getModel()));
            item.add(new DiscardButton(item.getModel()));
        }

        /**
         * Renders a JavaScript header item to the web response.
         *
         * @param response the response object
         */
        @Override
        public void renderHead(IHeaderResponse response) {
            super.renderHead(response);
            // initialize popovers
            String javaScript = "jQuery(\"[data-toggle='popover']\").popover(" +
                    "{ container: 'body', html: true, trigger: 'manual', " +
                    "content: function () { " +
                    "var content = jQuery(jQuery(this).data('html')); " +
                    "var dateString = jQuery(content).data('date'); " +
                    "var locale = jQuery(content).data('locale'); " +
                    "jQuery('.date', content).text(new Date(dateString).toLocaleDateString(locale, " +
                    "{ day: 'numeric', month: 'long', year: 'numeric' })); " +
                    "jQuery('.time', content).text(new Date(dateString).toLocaleTimeString(locale, " +
                    "{ hour: '2-digit', minute: '2-digit', hour12: false })); return content; " +
                    "} })" +
                    ".click(function () { jQuery(this).popover('toggle'); })" +
                    ".blur(function () { jQuery(this).popover('hide'); });";
            response.render(OnDomReadyHeaderItem.forScript(javaScript));
        }
    }

    /**
     * A button which sends the user to a page where inbound contexts are verified.
     */
    private class VerifyButton extends Link<InboundContextGroup> {
        /**
         * Creates a verify button.
         */
        public VerifyButton(IModel<InboundContextGroup> model) {
            super("verifyButton", model);
        }

        /**
         * Called on button click.
         */
        @Override
        public void onClick() {
            setResponsePage(new ContextVerificationPage(getModel()));
        }
    }

    /**
     * A button which discards inbound contexts matching an InboundContextGroup object.
     */
    private class DiscardButton extends AjaxLink<InboundContextGroup> {
        /**
         * Creates a discard button.
         *
         * @param model model of the button
         */
        public DiscardButton(IModel<InboundContextGroup> model) {
            super("discardButton", model);
        }

        /**
         * Called on button click.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            new InboundContextGroupDao().remove(getModelObject());
            target.add(InboundContextGroupPanel.this);
        }
    }
}
