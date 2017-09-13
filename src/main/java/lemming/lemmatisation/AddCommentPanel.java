package lemming.lemmatisation;

import lemming.auth.WebSession;
import lemming.context.Comment;
import lemming.context.Context;
import lemming.context.ContextDao;
import lemming.ui.panel.ModalFormPanel;
import lemming.user.User;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.CollectionModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A modal dialog to add a comment to a context.
 */
public class AddCommentPanel extends ModalFormPanel {
    /**
     * A data table.
     */
    private final LemmatisationDataTable dataTable;

    /**
     * A text area for content of comments.
     */
    private TextArea<String> commentTextArea;

    /**
     * Creates an add comment panel.
     *
     * @param dataTable a data table which delivers row models
     */
    public AddCommentPanel(LemmatisationDataTable dataTable) {
        super("addCommentPanel");
        this.dataTable = dataTable;
        commentTextArea = new TextArea<>("comment", Model.of(""));

        addFormComponent(commentTextArea);
    }

    /**
     * Returns a list of selected contexts.
     *
     * @return A list of selected contexts.
     */
    private List<Context> getSelectedContexts() {
        Collection<IModel<Context>> rowModels = dataTable.getRowModels();
        List<Context> selectedContexts = new ArrayList<>();

        for (IModel<Context> rowModel : rowModels) {
            if (rowModel.getObject().getSelected()) {
                Context context = rowModel.getObject();
                selectedContexts.add(context);
            }
        }

        return selectedContexts;
    }

    /**
     * Returns a list of IDs for a list of contexts.
     *
     * @param contexts
     * @return
     */
    private CollectionModel<Integer> getContextIds(List<Context> contexts) {
        CollectionModel<Integer> contextIds = new CollectionModel<>(new ArrayList<>());

        for (Context context : contexts) {
            contextIds.getObject().add(context.getId());
        }

        return contextIds;
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
                "jQuery('#" + getModalWindowId() + "').modal('show'); " +
                "event.preventDefault(); event.stopPropagation(); } });";
        response.render(OnDomReadyHeaderItem.forScript(javaScript));
    }

    /**
     * Returns the title string.
     *
     * @return A localized string.
     */
    @Override
    public String getTitleString() {
        return getString("AddCommentPanel.addComment");
    }

    /**
     * Confirms the dialog when clicked.
     *
     * @param target target that produces an Ajax response
     * @param form   form that is submitted
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onConfirm(AjaxRequestTarget target, Form<?> form) {
        ContextDao contextDao = new ContextDao();
        List<Context> selectedContexts = getSelectedContexts();
        List<Context> changedContexts = new ArrayList<>();
        String content = commentTextArea.getModelObject();
        User user = WebSession.get().getUser();

        // check if session has expired
        WebSession.get().checkSessionExpired();

        if (content != null) {
            Comment comment = new Comment(content, user);

            for (Context context : selectedContexts) {
                Context refreshedContext = contextDao.refresh(context);
                refreshedContext.getComments().add(comment);
                changedContexts.add(refreshedContext);
            }
        }

        if (changedContexts.size() > 0) {
            new ContextDao().batchMerge(changedContexts);
        }

        dataTable.updateSelectedContexts(getContextIds(selectedContexts));
        target.add(dataTable);
    }
}
