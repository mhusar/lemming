package lemming.lemmatization;

import lemming.auth.WebSession;
import lemming.character.CharacterHelper;
import lemming.context.*;
import lemming.data.GenericDataProvider;
import lemming.table.AutoShrinkBehavior;
import lemming.table.GenericRowSelectColumn;
import lemming.table.TextFilterColumn;
import lemming.ui.TitleLabel;
import lemming.ui.input.InputPanel;
import lemming.ui.page.EmptyBasePage;
import lemming.ui.panel.FeedbackPanel;
import lemming.ui.panel.HeaderPanel;
import lemming.ui.panel.ModalFormPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxChannel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.ThrottlingSettings;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

import javax.json.JsonArray;
import java.util.ArrayList;
import java.util.List;

/**
 * An index page that lists all available contexts in a data table for lemmatization.
 */
@AuthorizeInstantiation({ "SIGNED_IN" })
public class LemmatizationPage extends EmptyBasePage {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * True if the filter form shall be enabled.
     */
    private static final Boolean FILTER_FORM_ENABLED = false;

    /**
     * A form for contexts of a data table.
     */
    private Form<Context> contextForm;

    /**
     * A data table for contexts.
     */
    private LemmatizationDataTable dataTable;

    /**
     * Creates a lemmatization page.
     */
    public LemmatizationPage() {
        GenericDataProvider<Context> dataProvider = new GenericDataProvider<Context>(Context.class,
                new SortParam<String>("keyword", true));
        FilterForm<Context> filterForm = new FilterForm<Context>("filterForm", dataProvider);
        contextForm = new Form<Context>("contextForm");
        TextField<String> filterTextField = new TextField<String>("filterTextField", Model.of(""));
        WebMarkupContainer bodyContainer = new TransparentWebMarkupContainer("body");
        WebMarkupContainer container = new WebMarkupContainer("container");
        Fragment fragment;

        // check if the session is expired
        WebSession.get().checkSessionExpired();

        if (FILTER_FORM_ENABLED) {
            fragment = new Fragment("fragment", "withFilterForm", bodyContainer);
            dataTable = new LemmatizationDataTable("lemmatizationDataTable", getColumns(), dataProvider, filterForm);

            filterTextField.add(new FilterUpdatingBehavior(filterTextField, dataTable, dataProvider));
            contextForm.add(dataTable);
            filterForm.add(contextForm);
            fragment.add(filterForm);
        } else {
            fragment = new Fragment("fragment", "withoutFilterForm", bodyContainer);
            dataTable = new LemmatizationDataTable("lemmatizationDataTable", getColumns(), dataProvider);

            filterTextField.add(new FilterUpdatingBehavior(filterTextField, dataTable, dataProvider));
            contextForm.add(dataTable);
            fragment.add(contextForm);

            JsonArray characterData = CharacterHelper.getCharacterData();
            String characterDataString = characterData.toString();
            bodyContainer.add(AttributeModifier.append("data-characters", characterDataString));
        }

        add(bodyContainer);
        add(new FeedbackPanel("feedbackPanel"));
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

        InputPanel inputPanel = new InputPanel("inputPanel");
        Panel lemmatizationPanel = new LemmatizationPanel("lemmatizationPanel");
        ModalFormPanel setLemmaPanel = new SetLemmaPanel("setLemmaPanel", contextForm, dataTable);
        ModalFormPanel setPosPanel = new SetPosPanel("setPosPanel", contextForm, dataTable);

        contextForm.add(setLemmaPanel);
        contextForm.add(setPosPanel);
        lemmatizationPanel.add(new SetLemmaLink("setLemmaLink", setLemmaPanel));
        lemmatizationPanel.add(new SetPosLink("setPosLink", setPosPanel));
        add(new HeaderPanel("headerPanel", LemmatizationPage.class));
        add(inputPanel);
        add(lemmatizationPanel);
    }

    /**
     * Returns the list of columns of the data table.
     *
     * @return A list of columns.
     */
    private List<IColumn<Context, String>> getColumns() {
        List<IColumn<Context, String>> columns = new ArrayList<>();

        columns.add(new ContextRowSelectColumn(Model.of(""), "selected"));
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
         * @param propertyExpression property expression of a column
         */
        public ContextRowSelectColumn(IModel<String> displayModel, String propertyExpression) {
            super(displayModel, propertyExpression);
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
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * The text field used as filter component.
         */
        TextField<String> textField;

        /**
         * Data table displaying filtered data.
         */
        LemmatizationDataTable dataTable;

        /**
         * Data provider which delivers data for the table.
         */
        GenericDataProvider<Context> dataProvider;

        /**
         * Creates a behavior.
         *
         * @param textField
         *            text field used a filter component
         * @param dataTable
         *            data table displaying filtered data
         * @param dataProvider
         *            data provider which delivers data for the table.
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
         * @param target
         *            target that produces an Ajax response
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
     * A link which opens a set lemma dialog.
     */
    private final class SetLemmaLink extends AjaxLink<Context> {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Modal form panel which is shown on click.
         */
        private ModalFormPanel setLemmaPanel;

        /**
         * Creates a set lemma link.
         *
         * @param id ID of the link
         */
        public SetLemmaLink(String id, ModalFormPanel setLemmaPanel) {
            super(id);
            this.setLemmaPanel = setLemmaPanel;
        }

        /**
         * Called on click.
         *
         * @param  target target that produces an Ajax response
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
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Modal form panel which is shown on click.
         */
        private ModalFormPanel setPosPanel;

        /**
         * Creates a set part of speech link.
         *
         * @param id ID of the link
         */
        public SetPosLink(String id, ModalFormPanel setPosPanel) {
            super(id);
            this.setPosPanel = setPosPanel;
        }

        /**
         * Called on click.
         *
         * @param  target target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            setPosPanel.show(target);
        }
    }
}
