package lemming.ui.panel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * An panel which displays a message and a message type.
 */
public class AlertPanel extends Panel {
    /**
     * Alert panel types.
     */
    public enum Type {
        SUCCESS, INFO, WARNING, ERROR
    }

    /**
     * A label displaying a colon.
     */
    private final Label colonLabel;

    /**
     * Message label of the panel.
     */
    private final Label messageLabel;

    /**
     * Status label of the panel.
     */
    private final Label statusLabel;

    /**
     * Creates a alert panel.
     */
    public AlertPanel() {
        super("alertPanel");
        colonLabel = new Label("colonLabel");
        messageLabel = new Label("messageLabel");
        statusLabel = new Label("statusLabel");

        add(AttributeModifier.append("role", "alert"));
        add(colonLabel.setEscapeModelStrings(false).setDefaultModel(Model.of(":&#160;")).setOutputMarkupId(true)
                .setOutputMarkupPlaceholderTag(true));
        add(messageLabel.setOutputMarkupId(true).setEscapeModelStrings(false));
        add(statusLabel.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
        add(new CloseButton());
    }

    /**
     * Sets the message of an alert panel.
     *
     * @param message message text
     * @return The panel for method chaining.
     */
    public AlertPanel setMessage(String message) {
        messageLabel.setDefaultModel(Model.of(message));
        return this;
    }

    /**
     * Sets the type of an alert panel
     *
     * @param type type of an alert panel
     * @return The panel for method chaining.
     */
    public AlertPanel setType(AlertPanel.Type type) {
        String styleClass = "";

        switch (type) {
            case SUCCESS:
                styleClass = "alert alert-success alert-dismissible";
                statusLabel.setVisible(false);
                colonLabel.setVisible(false);
                break;
            case INFO:
                styleClass = "alert alert-info alert-dismissible";
                statusLabel.setVisible(false);
                colonLabel.setVisible(false);
                break;
            case WARNING:
                styleClass = "alert alert-warning alert-dismissible";
                statusLabel.setDefaultModel(Model.of(getString("AlertPanel.warning"))).setVisible(true);
                colonLabel.setVisible(true);
                break;
            case ERROR:
                styleClass = "alert alert-danger alert-dismissible";
                statusLabel.setDefaultModel(Model.of(getString("AlertPanel.error"))).setVisible(true);
                colonLabel.setVisible(true);
                break;
        }

        add(AttributeModifier.replace("class", styleClass));
        return this;
    }

    /**
     * A button which closes the panel on click.
     */
    private class CloseButton extends AjaxLink<Void> {
        /**
         * Creates a close button.
         */
        public CloseButton() {
            super("closeButton");
        }

        /**
         * Called on button click.
         *
         * @param target target that produces an Ajax response
         */
        @Override
        public void onClick(AjaxRequestTarget target) {
            AlertPanel.this.setVisible(false);
            target.add(AlertPanel.this);
        }
    }
}
