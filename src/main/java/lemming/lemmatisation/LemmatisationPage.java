package lemming.lemmatisation;

import lemming.auth.WebSession;
import lemming.context.*;
import lemming.data.GenericDataProvider;
import lemming.table.AutoShrinkBehavior;
import lemming.table.BadgeColumn;
import lemming.table.RowSelectColumn;
import lemming.table.TextFilterColumn;
import lemming.ui.DropdownButtonPanel;
import lemming.ui.TitleLabel;
import lemming.ui.input.InputPanel;
import lemming.ui.panel.FeedbackPanel;
import lemming.ui.panel.ModalFormPanel;
import lemming.ui.panel.SidebarPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxChannel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.ThrottlingSettings;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.util.time.Duration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An index page that lists all available contexts in a data table for lemmatisation.
 */
@AuthorizeInstantiation({"SIGNED_IN"})
public class LemmatisationPage extends LemmatisationBasePage {
    /**
     * True if the filter form shall be enabled.
     */
    private static final Boolean FILTER_FORM_ENABLED = false;

    /**
     * A data table for contexts.
     */
    private final LemmatisationDataTable dataTable;

    /**
     * A sidebar panel displaying comments of contexts.
     */
    private final CommentSidebar commentSidebar;

    /**
     * Creates a lemmatisation page.
     */
    public LemmatisationPage() {
        GenericDataProvider<Context> dataProvider = new GenericDataProvider<>(Context.class,
                new SortParam<>("keyword", true));
        FilterForm<Context> filterForm = new FilterForm<>("filterForm", dataProvider);
        TextField<String> filterValueTextField = new TextField<>("filterTextField", Model.of(""));
        TextField<String> filterPropertyTextField = new HiddenField<>("filterPropertyTextField", Model.of("keyword"));
        DropdownButtonPanel dropdownButtonPanel = new DropdownButtonPanel<Context>(getString("Context.keyword"),
                filterPropertyTextField, getColumns());
        WebMarkupContainer container = new WebMarkupContainer("container");
        Fragment fragment;

        // check if the session is expired
        WebSession.get().checkSessionExpired();

        if (FILTER_FORM_ENABLED) {
            fragment = new Fragment("fragment", "withFilterForm", this);
            dataTable = new LemmatisationDataTable(getColumns(), dataProvider, filterForm);

            filterValueTextField.add(new FilterUpdatingBehavior(filterValueTextField, filterPropertyTextField,
                    dataTable, dataProvider));
            filterPropertyTextField.add(new FilterUpdatingBehavior(filterValueTextField, filterPropertyTextField,
                    dataTable, dataProvider));
            filterForm.add(dataTable);
            fragment.add(filterForm);
        } else {
            fragment = new Fragment("fragment", "withoutFilterForm", this);
            dataTable = new LemmatisationDataTable(getColumns(), dataProvider);

            filterValueTextField.add(new FilterUpdatingBehavior(filterValueTextField, filterPropertyTextField,
                    dataTable, dataProvider));
            filterPropertyTextField.add(new FilterUpdatingBehavior(filterValueTextField, filterPropertyTextField,
                    dataTable, dataProvider));
            fragment.add(dataTable);
        }

        dropdownButtonPanel.setSelectEvent("input");
        filterValueTextField.add(new PageScrollingBehavior());
        commentSidebar = new CommentSidebar("commentSidebar", SidebarPanel.Orientation.RIGHT) {
            @Override
            public void onRemoveComment(IModel<Context> model, AjaxRequestTarget target) {
                Collection<IModel<Context>> rowModels = dataTable.getRowModels();
                CollectionModel<Integer> selectedContextIds = new CollectionModel<>(new ArrayList<>());

                for (IModel<Context> rowModel : rowModels) {
                    if (rowModel.getObject().getSelected()) {
                        selectedContextIds.getObject().add(rowModel.getObject().getId());
                    }
                }

                dataTable.updateSelectedContexts(selectedContextIds);
                target.add(dataTable);
            }
        };

        add(new FeedbackPanel());
        add(new InputPanel());
        add(filterValueTextField);
        add(filterPropertyTextField.setOutputMarkupId(true));
        add(dropdownButtonPanel);
        add(commentSidebar);
        add(container);
        container.add(fragment);
        // auto-shrink following and preceding text columns
        add(new AutoShrinkBehavior());
    }

    /**
     * Called when a lemmatisation page is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new TitleLabel(getString("LemmatisationPage.header")));
        Panel lemmatisationPanel = new LemmatisationPanel();
        ModalFormPanel setLemmaPanel = new SetLemmaPanel(dataTable);
        ModalFormPanel setPosPanel = new SetPosPanel(dataTable);
        ModalFormPanel addCommentPanel = new AddCommentPanel(dataTable);

        lemmatisationPanel.add(new SetLemmaLink(setLemmaPanel));
        lemmatisationPanel.add(new SetPosLink(setPosPanel));
        lemmatisationPanel.add(new MarkContextLink(dataTable));
        lemmatisationPanel.add(new GroupContextsLink(dataTable));
        lemmatisationPanel.add(new AddCommentLink(addCommentPanel));

        add(setLemmaPanel);
        add(setPosPanel);
        add(addCommentPanel);
        add(lemmatisationPanel);
    }

    /**
     * Returns the list of columns of the data table.
     *
     * @return A list of columns.
     */
    private List<IColumn<Context, String>> getColumns() {
        List<IColumn<Context, String>> columns = new ArrayList<>();

        columns.add(new ContextRowSelectColumn(Model.of("")));
        columns.add(new NumberTextFilterColumn(Model.of(getString("Context.number")), "number", "number"));
        columns.add(new TextFilterColumn(Model.of(getString("Context.lemma")), "lemmaString", "lemmaString"));
        columns.add(new TextFilterColumn<>(Model.of(getString("Context.pos")), "posString", "posString"));
        columns.add(new TextFilterColumn<>(Model.of(getString("Context.location")), "location", "location"));
        columns.add(new PrecedingContextTextFilterColumn(Model.of(getString("Context.preceding")), "preceding",
                "preceding"));
        columns.add(new KeywordTextFilterColumn(Model.of(getString("Context.keyword")), "keyword", "keyword"));
        columns.add(new FollowingContextTextFilterColumn(Model.of(getString("Context.following")), "following",
                "following"));
        columns.add(new ContextGroupStatusColumn(Model.of("")));
        columns.add(new ContextBadgeColumn(Model.of("")));

        return columns;
    }

    /**
     * A row selection column for contexts.
     */
    private class ContextRowSelectColumn extends RowSelectColumn<Context, Context, String> {
        /**
         * Creates a row selection column.
         *
         * @param displayModel title of a column
         */
        public ContextRowSelectColumn(IModel<String> displayModel) {
            super(displayModel, "selected");
        }

        /**
         * Returns the CSS class of a column.
         *
         * @return A CSS class.
         */
        @Override
        public String getCssClass() {
            return "hidden";
        }
    }

    /**
     * A group status column for contexts.
     */
    private class ContextGroupStatusColumn extends GroupStatusColumn {
        /**
         * Creates a context group status column.
         *
         * @param displayModel title of a column
         */
        public ContextGroupStatusColumn(IModel<String> displayModel) {
            super(displayModel, "group");
        }

        /**
         * Creates a context group status panel.
         *
         * @param panelId ID of the panel
         * @param model   model of the row item
         * @return A context group panel.
         */
        @Override
        public Panel createGroupPanel(String panelId, IModel<Context> model) {
            Context refreshedContext = new ContextDao().refresh(model.getObject());
            return new GroupStatusPanel(panelId, model);
        }

        /**
         * Called when a link inside a context group status panel is clicked.
         *
         * @param target target that produces an Ajax response
         * @param model  model of the row item
         */
        @Override
        public void onClick(AjaxRequestTarget target, IModel<Context> model) {
        }
    }

    /**
     * A badge column for contexts.
     */
    private class ContextBadgeColumn extends BadgeColumn<Context, Context, String> {
        /**
         * Creates a badge column.
         *
         * @param displayModel title of a column
         */
        public ContextBadgeColumn(IModel<String> displayModel) {
            super(displayModel, "badge");
        }

        /**
         * Creates a badge panel.
         *
         * @param panelId ID of the panel
         * @param model   model of the row item
         * @return A badge panel.
         */
        @Override
        public Panel createBadgePanel(String panelId, IModel<Context> model) {
            Context refreshedContext = new ContextDao().refresh(model.getObject());

            if (refreshedContext.getComments() != null && refreshedContext.getComments().size() > 0) {
                return new BadgePanel(panelId, model, String.valueOf(refreshedContext.getComments().size()), null);
            } else {
                return (Panel) new BadgePanel(panelId, model, null, "0").setVisible(false);
            }
        }

        /**
         * Called when a link inside a badge panel is clicked.
         *
         * @param target target that produces an Ajax response
         * @param model  model of the row item
         */
        @Override
        public void onClick(AjaxRequestTarget target, IModel<Context> model) {
            commentSidebar.refresh(model, target);
            commentSidebar.toggle(target);
        }
    }

    /**
     * Implementation of a form component updating behavior for a filter text field.
     */
    private class FilterUpdatingBehavior extends AjaxFormComponentUpdatingBehavior {
        /**
         * The text field used as value filter component.
         */
        final TextField<String> valueTextField;

        /**
         * The text field used as property filter component.
         */
        final TextField<String> propertyTextField;

        /**
         * Data table displaying filtered data.
         */
        final LemmatisationDataTable dataTable;

        /**
         * Data provider which delivers data for the table.
         */
        final GenericDataProvider<Context> dataProvider;

        /**
         * Creates a behavior.
         *
         * @param valueTextField    text field used a value filter component
         * @param propertyTextField text field used a property filter component
         * @param dataTable         data table displaying filtered data
         * @param dataProvider      data provider which delivers data for the table.
         */
        public FilterUpdatingBehavior(TextField<String> valueTextField, TextField<String> propertyTextField,
                                      LemmatisationDataTable dataTable, GenericDataProvider<Context> dataProvider) {
            super("input");
            this.valueTextField = valueTextField;
            this.propertyTextField = propertyTextField;
            this.dataTable = dataTable;
            this.dataProvider = dataProvider;
        }

        /**
         * Called when the text field content changes.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        protected void onUpdate(AjaxRequestTarget target) {
            dataProvider.updateFilter(valueTextField.getModelObject(), propertyTextField.getModelObject());
            target.add(dataTable);
        }

        /**
         * Modifies Ajax request attributes.
         *
         * @param attributes Ajax request attributes
         */
        @Override
        protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
            super.updateAjaxAttributes(attributes);
            attributes.setChannel(new AjaxChannel(getComponent().getId(), AjaxChannel.Type.DROP));
            attributes.setThrottlingSettings(new ThrottlingSettings(Duration.milliseconds(200)));
        }
    }

    /**
     * Adds a behavior to the filter text field which reacts to page scrolling.
     */
    private class PageScrollingBehavior extends Behavior {
        /**
         * Renders to the web response what the component wants to contribute.
         *
         * @param component component object
         * @param response  response object
         */
        @Override
        public void renderHead(Component component, IHeaderResponse response) {
            String javaScript = String.format("jQuery(window).scroll(function () { " +
                    "var focused = jQuery(':focus'), input = jQuery('#%s'); " +
                    "if (focused.length) { return; } " +
                    "if (input.length && input.isInViewport(input.height())) { " +
                    "input.focus(); } });", component.getMarkupId());
            response.render(OnDomReadyHeaderItem.forScript(javaScript));
        }
    }

    /**
     * A link which opens a set lemma dialog.
     */
    private final class SetLemmaLink extends AjaxLink<Context> {
        /**
         * Modal form panel which is shown on click.
         */
        private final ModalFormPanel setLemmaPanel;

        /**
         * Creates a set lemma link.
         */
        public SetLemmaLink(ModalFormPanel setLemmaPanel) {
            super("setLemmaLink");
            this.setLemmaPanel = setLemmaPanel;
        }

        /**
         * Called on click.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            setLemmaPanel.show(target);
        }
    }

    /**
     * A link which opens a set part of speech dialog.
     */
    private final class SetPosLink extends AjaxLink<Context> {
        /**
         * Modal form panel which is shown on click.
         */
        private final ModalFormPanel setPosPanel;

        /**
         * Creates a set part of speech link.
         */
        public SetPosLink(ModalFormPanel setPosPanel) {
            super("setPosLink");
            this.setPosPanel = setPosPanel;
        }

        /**
         * Called on click.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            setPosPanel.show(target);
        }
    }

    /**
     * A link which opens an add comment dialog.
     */
    private final class AddCommentLink extends AjaxLink<Context> {
        /**
         * Modal form panel which is shown on click.
         */
        private final ModalFormPanel addCommentPanel;

        /**
         * Creates a set comment link.
         */
        public AddCommentLink(ModalFormPanel addCommentPanel) {
            super("addCommentLink");
            this.addCommentPanel = addCommentPanel;
        }

        /**
         * Called on click.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            addCommentPanel.show(target);
        }
    }
}
