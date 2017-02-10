package lemming.pos;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.TextRequestHandler;

/**
 * A text field able to auto-complete part of speech names.
 */
public class PosAutoCompleteTextField extends PosTextField {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Maximum number of delivered results.
     */
    private static final Integer MAXIMUM_RESULTS = 10;

    /**
     * Creates a part of speech auto complete text field.
     * 
     * @param id
     *            ID of the text field
     */
    public PosAutoCompleteTextField(String id) {
        super(id);
        add(new PosAutoCompleteBehavior(id));
    }

    /**
     * Creates a part of speech auto complete text field.
     * 
     * @param id
     *            ID of the text field
     * @param model
     *            data model of the text field
     */
    public PosAutoCompleteTextField(String id, IModel<Pos> model) {
        super(id, model);
        add(new PosAutoCompleteBehavior(id));
    }

    /**
     * Implementation of an auto complete behavior.
     */
    private class PosAutoCompleteBehavior extends AbstractAjaxBehavior {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * ID of the auto complete text field.
         */
        private final String textFieldId;

        /**
         * Creates a new behavior.
         * 
         * @param id
         *            ID of the auto complete text field
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
                builder.add(posList.get(i).getName());
            }

            requestCycle
                    .scheduleRequestHandlerAfterCurrent(new TextRequestHandler("application/json",
                            "UTF-8", builder.build().toString()));
        }

        /**
         * Renders a JavaScript header item to the web response.
         * 
         * @param component
         *            component which is contributing to the response
         * @param response
         *            the response object
         */
        @Override
        public void renderHead(Component component, IHeaderResponse response) {
            super.renderHead(component, response);
            response.render(JavaScriptHeaderItem.forScript(
                    "var posSelector = \"#" + textFieldId + "\";\nvar posCallbackUrl = \""
                            + getCallbackUrl() + "\";", "posCallback"));
        }
    }
}
