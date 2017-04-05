package lemming.table;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * A panel with a paging navigator form which allows to directly jump to pages.
 *
 * @param <T> class type
 */
public class PagingNavigatorFormPanel<T> extends Panel {
    /**
     * Creates a paging navigator form panel.
     *
     * @param id ID of the panel
     * @param table the parent table
     */
    public PagingNavigatorFormPanel(String id, DataTable<T, String> table) {
        super(id);
        add(new PagingNavigatorForm("navigatorForm", table));
    }

    /**
     * A form which allows to directly jump to pages.
     */
    private class PagingNavigatorForm extends Form<Void> {
        /**
         * Creates a paging navigator form.
         *
         * @param id ID of the form
         * @param table the parent table
         */
        public PagingNavigatorForm(String id, DataTable<T, String> table) {
            super(id);
            TextField<String> pageTextField = new TextField<String>("page", Model.of(""));
            AjaxButton gotoButton = new GotoButton("gotoButton", table, pageTextField);
            add(pageTextField.setOutputMarkupId(true));
            add(gotoButton.setOutputMarkupId(true));
            setDefaultButton(gotoButton);
        }
    }

    /**
     * An Ajax button which sends the user to a different page.
     */
    private class GotoButton extends AjaxButton {
        /**
         * The parent table.
         */
        private DataTable<T, String> table;

        /**
         * A text field for page numbers.
         */
        private TextField<String> pageTextField;

        /**
         * Creates a go to button.
         *
         * @param id ID of the button
         * @param table the parent table
         * @param pageTextField a text field for page numbers
         */
        public GotoButton(String id, DataTable<T, String> table, TextField<String> pageTextField) {
            super(id);
            this.table = table;
            this.pageTextField = pageTextField;
        }

        /**
         * Called on form submit.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        protected void onSubmit(AjaxRequestTarget target) {
            String pageString = pageTextField.getModelObject();
            Long pageCount = table.getPageCount();
            Long currentPage = null;

            if (pageString != null && pageString.matches("\\d+")) {
                // data table pages are 0-based
                currentPage = Long.valueOf(pageString) - 1L;
                currentPage = (currentPage < 0L) ? 0L : currentPage;
                currentPage = (currentPage >= pageCount) ? pageCount - 1L : currentPage;
            }

            if (currentPage != null && pageCount > 0) {
                table.setCurrentPage(currentPage);
                target.add(table);
            }

            pageTextField.setModelObject("");
            target.add(pageTextField);
            target.add(this);
        }
    }
}
