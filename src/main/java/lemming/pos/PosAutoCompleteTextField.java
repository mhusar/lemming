package lemming.pos;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.TextRequestHandler;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import java.util.List;

/**
 * A text field able to auto-complete part of speech names.
 */
public class PosAutoCompleteTextField extends PosTextField {
    /**
     * Maximum number of delivered results.
     */
    private static final Integer MAXIMUM_RESULTS = 15;

    /**
     * Creates a part of speech auto complete text field.
     */
    public PosAutoCompleteTextField() {
        super("pos");
        add(new PosAutoCompleteBehavior("pos"));
    }

    /**
     * Creates a part of speech auto complete text field.
     *
     * @param model data model of the text field
     */
    public PosAutoCompleteTextField(IModel<Pos> model) {
        super("pos", model);
        add(new PosAutoCompleteBehavior("pos"));
    }

    /**
     * Implementation of an auto complete behavior.
     */
    private class PosAutoCompleteBehavior extends AbstractAjaxBehavior {
        /**
         * ID of the auto complete text field.
         */
        private final String textFieldId;

        /**
         * Creates a behavior.
         *
         * @param id ID of the auto complete text field
         */
        public PosAutoCompleteBehavior(String id) {
            textFieldId = id;
        }

        /**
         * Called when a request to a behavior is received.
         */
        @Override
        public void onRequest() {
            RequestCycle requestCycle = RequestCycle.get();
            Request request = requestCycle.getRequest();
            IRequestParameters requestParameters = request.getRequestParameters();
            String posName = requestParameters.getParameterValue("term").toString();

            JsonArrayBuilder builder = Json.createArrayBuilder();
            List<Pos> posList = new PosDao().findByNameStart(posName);

            for (int i = 0; i < Math.min(posList.size(), MAXIMUM_RESULTS); i++) {
                builder.add(Json.createObjectBuilder().add("label", posList.get(i).getName())
                        .add("value", posList.get(i).getName()));
            }

            requestCycle.scheduleRequestHandlerAfterCurrent(new TextRequestHandler("application/json", "UTF-8",
                    builder.build().toString()));
        }

        /**
         * Renders a JavaScript header item to the web response.
         *
         * @param component component which is contributing to the response
         * @param response  the response object
         */
        @Override
        public void renderHead(Component component, IHeaderResponse response) {
            String javaScript = "jQuery('#" + textFieldId + "').autocomplete({ " +
                    "autoFocus: true, delay: 0, source: '" + getCallbackUrl() + "', " +
                    "select: function (event, ui) { event.stopPropagation(); }, " +
                    "create: function () { " +
                    "jQuery(this).data('ui-autocomplete')._renderItem = function (ul, item) { " +
                    "var li = jQuery('<li></li>'); " +
                    "li.append('<div>' + item.label + '</div>').appendTo(ul); " +
                    "return li; }; }});";
            response.render(OnDomReadyHeaderItem.forScript(javaScript));
        }
    }
}
