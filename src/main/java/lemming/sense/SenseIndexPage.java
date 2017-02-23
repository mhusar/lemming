package lemming.sense;

import lemming.auth.WebSession;
import lemming.data.GenericDataProvider;
import lemming.lemma.Lemma;
import lemming.lemma.LemmaAutoCompleteTextField;
import lemming.lemma.LemmaDao;
import lemming.table.FilterUpdatingBehavior;
import lemming.table.GenericDataTable;
import lemming.table.TextFilterColumn;
import lemming.ui.page.BasePage;
import lemming.ui.panel.FeedbackPanel;
import lemming.ui.panel.ModalFormPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * An index page that lists all available senses in a data table.
 */
@AuthorizeInstantiation({ "SIGNED_IN" })
public class SenseIndexPage extends BasePage {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * True if the filter form shall be enabled.
     */
    private static final Boolean FILTER_FORM_ENABLED = false;

    /**
     * Creates a sense index page.
     */
    public SenseIndexPage() {
        GenericDataProvider<Sense> dataProvider = new GenericDataProvider<Sense>(Sense.class,
                new SortParam<String>("lemmaString", true));
        FilterForm<Sense> filterForm = new FilterForm<Sense>("filterForm", dataProvider);
        TextField<String> filterTextField = new TextField<String>("filterTextField", Model.of(""));
        WebMarkupContainer container = new WebMarkupContainer("container");
        Fragment fragment;
        GenericDataTable<Sense> dataTable;
        ChooseLemmaPanel chooseLemmaPanel = new ChooseLemmaPanel("chooseLemmaPanel");

        // check if the session is expired
        WebSession.get().checkSessionExpired();

        if (FILTER_FORM_ENABLED) {
            fragment = new Fragment("fragment", "withFilterForm", this);
            dataTable = new GenericDataTable<Sense>("senseDataTable", getColumns(), dataProvider, filterForm);

            filterTextField.add(new FilterUpdatingBehavior(filterTextField, dataTable, dataProvider));
            filterForm.add(dataTable);
            fragment.add(filterForm);
        } else {
            fragment = new Fragment("fragment", "withoutFilterForm", this);
            dataTable = new GenericDataTable<Sense>("senseDataTable", getColumns(), dataProvider);

            filterTextField.add(new FilterUpdatingBehavior(filterTextField, dataTable, dataProvider));
            fragment.add(dataTable);
        }

        add(new SenseDeleteConfirmPanel("senseDeleteConfirmPanel", dataTable));
        add(new FeedbackPanel("feedbackPanel"));
        add(filterTextField);
        add(new NewButton("new", chooseLemmaPanel));
        add(container);
        container.add(fragment);
        add(chooseLemmaPanel);
    }

    /**
     * Returns the list of columns of the data table.
     * 
     * @return A list of columns.
     */
    private List<IColumn<Sense, String>> getColumns() {
        List<IColumn<Sense, String>> columns = new ArrayList<IColumn<Sense, String>>();

        columns.add(new TextFilterColumn<Sense, Sense, String>(Model.of(getString("Sense.lemma")),
                "lemmaString", "lemmaString"));
        columns.add(new TextFilterColumn<Sense, Sense, String>(Model.of(getString("Sense.meaning")),
                "meaning", "meaning"));
        columns.add(new SenseActionPanelColumn(Model.of("")));

        return columns;
    }

    /**
     * A button which asks to choose a lemma.
     */
    private class NewButton extends AjaxLink<Void> {
        /**
         * Determines if a deserialized file is compatible with this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * A panel used to choose a lemma for sense editing.
         */
        private ModalFormPanel chooseLemmaPanel;

        /**
         * Creates a button.
         *
         * @param id ID of the button
         * @param chooseLemmaPanel a panel used to choose a lemma for sense editing
         */
        public NewButton(String id, ModalFormPanel chooseLemmaPanel) {
            super(id);
            this.chooseLemmaPanel = chooseLemmaPanel;
        }

        /**
         * Called on button click.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            chooseLemmaPanel.show(target);
        }
    }

    /**
     * A panel used to choose a lemma for sense editing.
     */
    private class ChooseLemmaPanel extends ModalFormPanel {
        /**
         * A auto-complete textfield for lemmata.
         */
        private LemmaAutoCompleteTextField lemmaTextField;

        /**
         * Creates a choose lemma panel.
         *
         * @param id ID of the panel
         */
        public ChooseLemmaPanel(String id) {
            super(id, null);
            lemmaTextField = new LemmaAutoCompleteTextField("lemma", new Model<Lemma>());
            addFormComponent(lemmaTextField);
        }

        /**
         * Returns the button text of the confirmation button.
         *
         * @return A confirmation string.
         */
        @Override
        public String getConfirmationString() {
            return getString("ChooseLemmaPanel.okay");
        }

        /**
         * Returns the title string.
         *
         * @return A localized string.
         */
        @Override
        public String getTitleString() {
            return getString("ChooseLemmaPanel.chooseLemma");
        }

        /**
         * Confirms the dialog when clicked.
         *
         * @param target target that produces an Ajax response
         * @param form form that is submitted
         */
        @Override
        public void onConfirm(AjaxRequestTarget target, Form<?> form) {
            String lemmaName = lemmaTextField.getInput();
            Lemma lemma = new LemmaDao().findByName(lemmaName);

            if (lemma instanceof Lemma) {
                setResponsePage(new SenseEditPage(new Model<Lemma>(lemma), SenseIndexPage.class));
            }
        }
    }
}
