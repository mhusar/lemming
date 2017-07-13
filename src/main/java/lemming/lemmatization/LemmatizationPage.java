package lemming.lemmatization;

import lemming.auth.WebSession;
import lemming.context.*;
import lemming.data.GenericDataProvider;
import lemming.table.AutoShrinkBehavior;
import lemming.table.GenericRowSelectColumn;
import lemming.table.TextFilterColumn;
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
        TextField<String> filterTextField = new TextField<>("filterTextField", Model.of(""));
        WebMarkupContainer container = new WebMarkupContainer("container");
        Fragment fragment;

        // check if the session is expired
        WebSession.get().checkSessionExpired();

        if (FILTER_FORM_ENABLED) {
            fragment = new Fragment("fragment", "withFilterForm", this);
            dataTable = new LemmatizationDataTable(getColumns(), dataProvider, filterForm);

            filterTextField.add(new FilterUpdatingBehavior(filterTextField, dataTable, dataProvider));
            filterForm.add(dataTable);
            fragment.add(filterForm);
        } else {
            fragment = new Fragment("fragment", "withoutFilterForm", this);
            dataTable = new LemmatizationDataTable(getColumns(), dataProvider);

            filterTextField.add(new FilterUpdatingBehavior(filterTextField, dataTable, dataProvider));
            fragment.add(dataTable);
        }

        filterTextField.add(new PageScrollingBehavior());
        add(new FeedbackPanel());
        add(new InputPanel());
        add(filterTextField);
        add(container);
        container.add(fragment);
        // auto-shrink following and preceding text columns
        add(new AutoShrinkBehavior());
    }

    /**
     * Called when a base page is initialized.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new TitleLabel(getString("LemmatizationPage.header")));
        Panel lemmatizationPanel = new LemmatizationPanel();
        ModalFormPanel setLemmaPanel = new SetLemmaPanel(dataTable);
        ModalFormPanel setPosPanel = new SetPosPanel(dataTable);

        lemmatizationPanel.add(new SetLemmaLink(setLemmaPanel));
        lemmatizationPanel.add(new SetPosLink(setPosPanel));

        add(setLemmaPanel);
        add(setPosPanel);
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

        return columns;
    }

    /**
     * A row selection column for contexts.
     */
    private class ContextRowSelectColumn extends GenericRowSelectColumn<Context, Context, String> {
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
     * Implementation of a form component updating behavior for a filter text field.
     */
    private class FilterUpdatingBehavior extends AjaxFormComponentUpdatingBehavior {
        /**
         * The text field used as filter component.
         */
        final TextField<String> textField;

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
         * @param textField    text field used a filter component
         * @param dataTable    data table displaying filtered data
         * @param dataProvider data provider which delivers data for the table.
         */
        public FilterUpdatingBehavior(TextField<String> textField, LemmatizationDataTable dataTable,
                                      GenericDataProvider<Context> dataProvider) {
            super("input");
            this.textField = textField;
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
            dataProvider.updateFilter(textField.getInput());
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
}
