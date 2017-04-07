package lemming.ui.page;

import lemming.ui.panel.ModalFormPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

/**
 * A base page with header and input panels. TextFields on index pages are ordered by attribute tabindex.
 */
public class IndexBasePage extends BasePage {
    /**
     * Creates an index base page.
     */
    public IndexBasePage() {
        super();
    }

    /**
     * Called when an index base page is configured.
     */
    @Override
    protected void onConfigure() {
        super.onConfigure();
        visitChildren(TextField.class, new IVisitor<TextField<?>, Void>() {
            private Integer tabIndex = 1;

            @Override
            public void component(TextField<?> textField, IVisit<Void> visit) {
                if (textField.findParent(ModalFormPanel.class) != null) {
                    return;
                }

                textField.add(AttributeModifier.replace("tabindex", String.valueOf(tabIndex++)));
            }
        });
    }
}
