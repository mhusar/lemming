package lemming.context.inbound;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
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

        placeholder.add(new Label("label", getString("InboundContextSummaryPanel.placeholder")));
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
         * Populates the given item container.
         *
         * @param item item which is populated
         */
        @Override
        protected void populateItem(Item<InboundContextSummary> item) {
            InboundContextSummary summary = item.getModel().getObject();
            Instant instant = summary.getTimestamp().toInstant();
            ZonedDateTime dateTime = ZonedDateTime.ofInstant(instant, ZoneId.of("Europe/London"));

            String numberOfContexts = String.valueOf(summary.getNumberOfContexts());
            String localizedTime = dateTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                    .withLocale(getLocale()));
            String localizedDate = dateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                    .withLocale(getLocale()));
            String userString = summary.getUserString();
            StringResourceModel stringResourceModel = new StringResourceModel("InboundContextSummaryView.label", this)
                    .setParameters(numberOfContexts, userString, localizedTime, localizedDate);

            item.add(new Label("label", stringResourceModel.getString()));
        }
    }
}
