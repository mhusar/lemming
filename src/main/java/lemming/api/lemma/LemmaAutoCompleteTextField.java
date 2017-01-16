package lemming.api.lemma;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.TextRequestHandler;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import java.util.List;

/**
 * A text field able to auto-complete lemma names.
 */
public class LemmaAutoCompleteTextField extends LemmaTextField {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Maximum number of delivered results.
     */
    private static final Integer MAXIMUM_RESULTS = 10;

    /**
     * Creates a lemma auto complete text field.
     *
     * @param id
     *            ID of the text field
     */
    public LemmaAutoCompleteTextField(String id) {
        super(id);
        add(new LemmaAutoCompleteBehavior(id));
    }

    /**
     * Creates a lemma auto complete text field.
     *
     * @param id
     *            ID of the text field
     * @param model
     *            data model of the text field
     */
    public LemmaAutoCompleteTextField(String id, IModel<Lemma> model) {
        super(id, model);
        add(new LemmaAutoCompleteBehavior(id));
    }

    /**
     * Implementation of an auto complete behavior.
     */
    private class LemmaAutoCompleteBehavior extends AbstractAjaxBehavior {
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
        public LemmaAutoCompleteBehavior(String id) {
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
            String lemmaName = requestParameters.getParameterValue("term").toString();

            JsonArrayBuilder builder = Json.createArrayBuilder();
            List<Lemma> lemmaList = new LemmaDao().findByNameStart(lemmaName);

            for (int i = 0; i < Math.min(lemmaList.size(), MAXIMUM_RESULTS); i++) {
                builder.add(lemmaList.get(i).getName());
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
                    "var lemmaSelector = \"#" + textFieldId + "\";\nvar lemmaCallbackUrl = \""
                            + getCallbackUrl() + "\";", "lemmaCallback"));
        }
    }
}
