package lemming.context.inbound;

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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A panel which lists summaries about groups of inbound context data.
 */
public class InboundContextSummaryPanel extends Panel {
    /**
     * A placeholder that is displayed when the summary view is empty.
     */
    private MarkupContainer placeholder;

    /**
     * A RefreshingView for entities of class InboundContextSummary.
     */
    private InboundContextSummaryView summaryView;

    /**
     * Creates an InboundContextSummaryPanel.
     *
     * @param id ID of the panel
     */
    public InboundContextSummaryPanel(String id) {
        super(id);
        placeholder = new WebMarkupContainer("placeholder");
        summaryView = new InboundContextSummaryView("inboundContextSummaryView");
        add(placeholder);
        add(summaryView);
    }

    /**
     * Called when a InboundContextSummaryPanel is configured.
     */
    @Override
    protected void onConfigure() {
        super.onConfigure();
        placeholder.setVisible(!summaryView.getItemModels().hasNext());
    }

    /**
     * A RefreshingView for entities of class InboundContextSummary.
     */
    private class InboundContextSummaryView extends RefreshingView<InboundContextSummary> {
        /**
         * Create an InboundContextSummaryView.
         *
         * @param id ID of the view
         */
        public InboundContextSummaryView(String id) {
            super(id);
        }

        /**
         * Returns an iterator over models for items that will be added to this view.
         *
         * @return An iterator over models.
         */
        @Override
        protected Iterator<IModel<InboundContextSummary>> getItemModels() {
            List<IModel<InboundContextSummary>> summaryModelList = new ArrayList<IModel<InboundContextSummary>>();

            for (InboundContextSummary summary : new InboundContextDao().getSummaries()) {
                summaryModelList.add(new Model<InboundContextSummary>(summary));
            }

            return summaryModelList.iterator();
        }

        /**
         * Creates a location string based on begin and end location.
         *
         * @param beginLocation begin location
         * @param endLocation end location
         * @return A location string.
         */
        private String createLocationString(String beginLocation, String endLocation) {
            String[] beginStrings = beginLocation.split("_"), endStrings = endLocation.split("_");
            String beginDocument = beginStrings[0], endDocument = endStrings[0],
                    beginRubric = beginStrings[1].replaceFirst("^0+", ""),
                    endRubric = endStrings[1].replaceFirst("^0+", "");

            if (beginDocument.equals(endDocument)) {
                return new StringResourceModel("InboundContextSummaryView.location1", this)
                        .setParameters(beginDocument, beginRubric, endRubric).getString();
            } else {
                return new StringResourceModel("InboundContextSummaryView.location2", this)
                        .setParameters(beginDocument, beginRubric, endDocument, endRubric).getString();
            }
        }

        /**
         * Populates the given item container.
         *
         * @param item item which is populated
         */
        @Override
        protected void populateItem(Item<InboundContextSummary> item) {
            InboundContextSummary summary = item.getModel().getObject();
            Instant instant = summary.getTimestamp().toInstant();
            ZonedDateTime dateTime = ZonedDateTime.ofInstant(instant, ZoneId.of("Europe/London"));

            String numberOfContexts = String.valueOf(summary.getNumberOfContexts()),
                    locationString = createLocationString(summary.getBeginLocation(), summary.getEndLocation()),
                    userString = summary.getUserString(),
                    localizedDate = dateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                            .withLocale(getLocale())),
                    localizedTime = dateTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                            .withLocale(getLocale()));

            String itemLabel = new StringResourceModel("InboundContextSummaryView.itemLabel", this)
                    .setParameters(numberOfContexts, locationString).getString();
            String popoverContent = new StringResourceModel("InboundContextSummaryView.popoverContent", this)
                    .setParameters("<dl class='dl-horizontal'><dt>", "</dt><dd>" + userString + "</dd><dt>",
                            "</dt><dd>" + localizedDate + "</dd><dt>", "</dt><dd>" + localizedTime + "</dd><dt>")
                    .getString();

            item.add(new Label("itemLabel", itemLabel));
            item.add(new WebMarkupContainer("detailsButton").add(AttributeModifier.append("data-content",
                    popoverContent)));
            item.add(new ApproveButton("approveButton", item.getModel()));
            item.add(new DiscardButton("discardButton", item.getModel()));
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
                    "{ container: 'body', html: true, trigger: 'manual' })" +
                    ".click(function () { jQuery(this).popover('toggle'); })" +
                    ".blur(function () { jQuery(this).popover('hide'); });";
            response.render(OnDomReadyHeaderItem.forScript(javaScript));
        }
    }

    /**
     * A button which sends the user to a page where contexts are approved.
     */
    private class ApproveButton extends Link<InboundContextSummary> {
        /**
         * Creates an approve button.
         */
        public ApproveButton(String id, IModel<InboundContextSummary> model) {
            super(id, model);
        }

        /**
         * Called on button click.
         */
        @Override
        public void onClick() {
        }
    }

    /**
     * A but which discards inbound contexts matching inbound context summary.
     */
    private class DiscardButton extends AjaxLink<InboundContextSummary> {
        /**
         * Creates a discard button.
         *
         * @param id ID of the button
         * @param model model of the button
         */
        public DiscardButton(String id, IModel<InboundContextSummary> model) {
            super(id, model);
        }

        /**
         * Called on button click.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            new InboundContextDao().removeBySummary(getModelObject());
            target.add(InboundContextSummaryPanel.this);
        }
    }
}
