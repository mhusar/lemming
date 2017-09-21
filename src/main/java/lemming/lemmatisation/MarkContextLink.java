package lemming.lemmatisation;

import lemming.context.Context;
import lemming.context.ContextDao;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.CollectionModel;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Toggles the interesting flag of selected contexts.
 */
public class MarkContextLink extends AjaxLink<Void> {
    /**
     * A data table.
     */
    private final LemmatisationDataTable dataTable;

    /**
     * Creates a mark context link.
     *
     * @param dataTable a data table which delivers row models
     */
    public MarkContextLink(LemmatisationDataTable dataTable) {
        super("markContextLink");
        this.dataTable = dataTable;
        setOutputMarkupId(true);
    }

    /**
     * Renders to the web response what the component wants to contribute.
     *
     * @param response response object
     */
    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        // ctrl + m
        String javaScript = "jQuery(window).on('keydown', function (event) { " +
                "var modifier = event.ctrlKey || event.metaKey; " +
                "if (modifier && event.which === 77) { " +
                "jQuery('#" + getMarkupId() + "').click(); " +
                "event.preventDefault(); event.stopPropagation(); } });";
        response.render(OnDomReadyHeaderItem.forScript(javaScript));
    }

    /**
     * Called on click.
     *
     * @param target target that produces an Ajax response
     */
    @Override
    public void onClick(AjaxRequestTarget target) {
        Collection<IModel<Context>> rowModels = dataTable.getRowModels();
        CollectionModel<Integer> selectedContextIds = new CollectionModel<>(new ArrayList<>());
        ContextDao contextDao = new ContextDao();

        for (IModel<Context> rowModel : rowModels) {
            if (rowModel.getObject().getSelected()) {
                Context context = rowModel.getObject();
                context.setInteresting(!context.getInteresting());
                contextDao.merge(context);
                selectedContextIds.getObject().add(context.getId());
            }
        }

        dataTable.updateSelectedContexts(selectedContextIds);
        target.add(dataTable);
    }
}
