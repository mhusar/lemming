package lemming.context.inbound;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
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
 * A panel which lists packages of inbound contexts.
 */
public class InboundContextPackagePanel extends Panel {
    /**
     * A placeholder that is displayed when the context package view is empty.
     */
    private final MarkupContainer placeholder;

    /**
     * A RefreshingView for entities of class InboundContextPackage.
     */
    private final InboundContextPackageView contextPackageView;

    /**
     * Creates an InboundContextPackagePanel.
     */
    public InboundContextPackagePanel() {
        super("contextPackagePanel");
        placeholder = new WebMarkupContainer("placeholder");
        contextPackageView = new InboundContextPackageView();

        add(placeholder);
        add(contextPackageView);
    }

    /**
     * Called when a InboundContextPackagePanel is configured.
     */
    @Override
    protected void onConfigure() {
        super.onConfigure();
        placeholder.setVisible(!contextPackageView.getItemModels().hasNext());
    }

    /**
     * A RefreshingView for entities of class InboundContextPackage.
     */
    private class InboundContextPackageView extends RefreshingView<InboundContextPackage> {
        /**
         * Create an InboundContextPackageView.
         */
        public InboundContextPackageView() {
            super("contextPackageView");
        }

        /**
         * Returns an iterator over models for items that will be added to this view.
         *
         * @return An iterator over models.
         */
        @Override
        protected Iterator<IModel<InboundContextPackage>> getItemModels() {
            List<IModel<InboundContextPackage>> itemModels = new ArrayList<>();

            for (InboundContextPackage contextPackage : new InboundContextPackageDao().getAll()) {
                itemModels.add(new Model<>(contextPackage));
            }

            return itemModels.iterator();
        }

        /**
         * Creates a location string based on begin and end location.
         *
         * @param contextPackageDao Data Access Object for inbound context packages
         * @param contextPackage    package of inbound contexts
         * @return A location string.
         */
        private String createLocationString(InboundContextPackageDao contextPackageDao, InboundContextPackage contextPackage) {
            String beginLocation = contextPackageDao.getBeginLocation(contextPackage);
            String endLocation = contextPackageDao.getEndLocation(contextPackage);
            String[] beginStrings = beginLocation.split("_"), endStrings = endLocation.split("_");
            String beginDocument = beginStrings[0], endDocument = endStrings[0],
                    beginRubric = beginStrings[1].replaceFirst("^0+", ""),
                    endRubric = endStrings[1].replaceFirst("^0+", "");

            if (beginDocument.equals(endDocument)) {
                return new StringResourceModel("InboundContextPackageView.location1", this)
                        .setParameters(beginDocument, beginRubric, endRubric).getString();
            } else {
                return new StringResourceModel("InboundContextPackageView.location2", this)
                        .setParameters(beginDocument, beginRubric, endDocument, endRubric).getString();
            }
        }

        /**
         * Populates the given item container.
         *
         * @param item item which is populated
         */
        @Override
        protected void populateItem(Item<InboundContextPackage> item) {
            InboundContextPackageDao contextPackageDao = new InboundContextPackageDao();
            InboundContextPackage contextPackage = item.getModel().getObject();
            Instant instant = contextPackage.getCreated().toInstant();
            String contextCount = String.valueOf(contextPackageDao.getContexts(contextPackage).size()),
                    location = createLocationString(contextPackageDao, contextPackage),
                    user = contextPackage.getUser().getRealName();
            String itemLabel = new StringResourceModel("InboundContextPackageView.itemLabel", this)
                    .setParameters(contextCount, location).getString();
            String popoverContent = new StringResourceModel("InboundContextPackageView.popoverContent", this)
                    .setParameters(String.format("<dl class='dl-horizontal' data-date='%s' data-locale='%s'><dt>",
                            instant.toString(), getString("InboundContextPackageView.popoverLocale")),
                            String.format("</dt><dd>%s</dd><dt>", user), "</dt><dd class='date'></dd><dt>",
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
    private class VerifyButton extends IndicatingAjaxLink<InboundContextPackage> {
        /**
         * Creates a verify button.
         */
        public VerifyButton(IModel<InboundContextPackage> model) {
            super("verifyButton", model);
        }

        /**
         * Called on button click.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            InboundContextPackageDao inboundContextPackageDao = new InboundContextPackageDao();

            if (!inboundContextPackageDao.hasMatchedContexts(getModelObject())) {
                inboundContextPackageDao.matchContextsByHash(getModelObject());
            }

            setResponsePage(new InboundContextVerificationPage(getModel()));
        }

        /**
         * Returns the markup ID of the Ajax indicator.
         *
         * @return A component markup id.
         */
        @Override
        public String getAjaxIndicatorMarkupId() {
            return "indicatorOverlayPanel";
        }
    }

    /**
     * A button which discards inbound contexts matching an InboundContextPackage object.
     */
    private class DiscardButton extends IndicatingAjaxLink<InboundContextPackage> {
        /**
         * Creates a discard button.
         *
         * @param model model of the button
         */
        public DiscardButton(IModel<InboundContextPackage> model) {
            super("discardButton", model);
        }

        /**
         * Called on button click.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            new InboundContextPackageDao().remove(getModelObject());
            target.add(InboundContextPackagePanel.this);
        }

        /**
         * Returns the markup ID of the Ajax indicator.
         *
         * @return A component markup id.
         */
        @Override
        public String getAjaxIndicatorMarkupId() {
            return "indicatorOverlayPanel";
        }
    }
}
