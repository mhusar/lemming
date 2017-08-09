package lemming.lemmatization;

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
import org.apache.wicket.util.time.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * An index page that lists all available contexts in a data table for lemmatization.
 */
@AuthorizeInstantiation({"SIGNED_IN"})
public class LemmatizationPage extends LemmatizationBasePage {
    /**
     * True if the filter form shall be enabled.
     */
    private static final Boolean FILTER_FORM_ENABLED = false;

    /**
     * A data table for contexts.
     */
    private final LemmatizationDataTable dataTable;

    /**
     * Creates a lemmatization page.
     */
    public LemmatizationPage() {
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
            dataTable = new LemmatizationDataTable(getColumns(), dataProvider, filterForm);

            filterValueTextField.add(new FilterUpdatingBehavior(filterValueTextField, filterPropertyTextField,
                    dataTable, dataProvider));
            filterPropertyTextField.add(new FilterUpdatingBehavior(filterValueTextField, filterPropertyTextField,
                    dataTable, dataProvider));
            filterForm.add(dataTable);
            fragment.add(filterForm);
        } else {
            fragment = new Fragment("fragment", "withoutFilterForm", this);
            dataTable = new LemmatizationDataTable(getColumns(), dataProvider);

            filterValueTextField.add(new FilterUpdatingBehavior(filterValueTextField, filterPropertyTextField,
                    dataTable, dataProvider));
            filterPropertyTextField.add(new FilterUpdatingBehavior(filterValueTextField, filterPropertyTextField,
                    dataTable, dataProvider));
            fragment.add(dataTable);
        }

        dropdownButtonPanel.setSelectEvent("input");
        filterValueTextField.add(new PageScrollingBehavior());
        add(new FeedbackPanel());
        add(new InputPanel());
        add(filterValueTextField);
        add(filterPropertyTextField.setOutputMarkupId(true));
        add(dropdownButtonPanel);
        add(container);
        container.add(fragment);
        // auto-shrink following and preceding text columns
        add(new AutoShrinkBehavior());
    }

    /**
     * Called when a lemmatization page is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new TitleLabel(getString("LemmatizationPage.header")));
        Panel lemmatizationPanel = new LemmatizationPanel();
        ModalFormPanel setLemmaPanel = new SetLemmaPanel(dataTable);
        ModalFormPanel setPosPanel = new SetPosPanel(dataTable);
        ModalFormPanel addCommentPanel = new AddCommentPanel(dataTable);

        lemmatizationPanel.add(new SetLemmaLink(setLemmaPanel));
        lemmatizationPanel.add(new SetPosLink(setPosPanel));
        lemmatizationPanel.add(new AddCommentLink(addCommentPanel));

        add(setLemmaPanel);
        add(setPosPanel);
        add(addCommentPanel);
        add(lemmatizationPanel);
    }

    /**
     * Returns the list of columns of the data table.
     *
     * @return A list of columns.
     */
    private List<IColumn<Context, String>> getColumns() {
        List<IColumn<Context, String>> columns = new ArrayList<>();

        columns.add(new ContextRowSelectColumn(Model.of("")));
        columns.add(new LemmaTextFilterColumn(Model.of(getString("Context.lemma")),
                "lemmaString", "lemmaString"));
        columns.add(new TextFilterColumn<>(Model.of(getString("Context.pos")),
                "posString", "posString"));
        columns.add(new TextFilterColumn<>(Model.of(getString("Context.location")),
                "location", "location"));
        columns.add(new PrecedingContextTextFilterColumn(Model.of(getString("Context.preceding")), "preceding",
                "preceding"));
        columns.add(new KeywordTextFilterColumn(Model.of(getString("Context.keyword")), "keyword",
                "keyword"));
        columns.add(new FollowingContextTextFilterColumn(Model.of(getString("Context.following")), "following",
                "following"));
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
     * A badge column for contexts.
     */
    private class ContextBadgeColumn extends BadgeColumn<Context, Context, String> {
        /**
         * Creates a badge column
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
                return new BadgePanel(panelId, String.valueOf(refreshedContext.getComments().size()), null);
            } else {
                return new BadgePanel(panelId, null, "0");
            }
        }

        /**
         * Called when a link inside a badge panel is clicked.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
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
        final LemmatizationDataTable dataTable;

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
                                      LemmatizationDataTable dataTable, GenericDataProvider<Context> dataProvider) {
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
