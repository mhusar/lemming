package lemming.ui;

import lemming.data.Tuple;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * A dropdown panel based on Bootstrap’s dropdown button.
 *
 * @param <T> data type
 */
public class DropdownButtonPanel<T> extends Panel {
    /**
     * Label of the button.
     */
    private String buttonLabel;

    /**
     * String representation of the select event.
     */
    private String event;

    /**
     * Hidden text field the selected value is applied to.
     */
    private TextField<String> hiddenTextField;

    /**
     * Labels of the dropdown list.
     */
    private ArrayList<String> labels;

    /**
     * Values of the dropdown list.
     */
    private ArrayList<String> values;

    /**
     * Initialized state set in onConfigure();
     */
    private Boolean wasInitialized = false;

    /**
     * Creates a dropdown button panel.
     *
     * @param buttonLabel label of the button
     * @param hiddenTextField hiddent text field
     * @param data labels and values of the dropdown list
     */
    public DropdownButtonPanel(String buttonLabel, TextField<String> hiddenTextField,
                               ArrayList<Tuple<String, String>> data) {
        super("dropdownButtonPanel");
        this.buttonLabel = buttonLabel;
        this.hiddenTextField = hiddenTextField;

        for (Tuple<String, String> tuple : data) {
            this.labels.add(tuple.getKey());
            this.values.add(tuple.getValue());
        }
    }

    /**
     * Creates a dropdown button panel from a columns list.
     *
     * @param buttonLabel label of the button
     * @param hiddenTextField hiddent text field
     * @param columns columns of type IColumn
     */
    public DropdownButtonPanel(String buttonLabel, TextField<String> hiddenTextField,
                               List<IColumn<T, String>> columns) {
        super("dropdownButtonPanel");
        this.buttonLabel = buttonLabel;
        this.hiddenTextField = hiddenTextField;
        this.labels = new ArrayList<>();
        this.values = new ArrayList<>();

        for (IColumn<T, String> column : columns) {
            if (column instanceof AbstractColumn) {
                String label = ((AbstractColumn<T, String>) column).getDisplayModel().getObject();
                String value = column.getSortProperty();

                if (label != null && !label.isEmpty()) {
                    labels.add(label);
                    values.add(value);
                }
            }
        }
    }

    /**
     * Sets the event that is fired on select.
     *
     * @param event select event
     */
    public void setSelectEvent(String event) {
        this.event = event;
    }

    /**
     * Called when a dropdown button panel is configured.
     */
    @Override
    protected void onConfigure() {
        super.onConfigure();

        if (!wasInitialized) {
            Button button = new Button("button");
            IModel<String> labelModel = Model.of(buttonLabel);
            Label label = new Label("buttonLabel", labelModel);

            add(new AttributeModifier("class", "input-group-btn"));
            button.add(label.setOutputMarkupId(true));
            add(button);
            add(new Loop("list", labels.size()) {
                @Override
                protected void populateItem(LoopItem item) {
                    item.add(new AjaxLink<T>("link") {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            hiddenTextField.getModel().setObject(values.get(item.getIndex()));
                            labelModel.setObject(labels.get(item.getIndex()));

                            target.add(hiddenTextField);
                            target.add(label);

                            if (event != null) {
                                String javascript = String.format("jQuery('#%s').trigger('input');",
                                        hiddenTextField.getMarkupId());
                                target.appendJavaScript(javascript);
                            }
                        }
                    }.add(new Label("label", labels.get(item.getIndex()))));
                }
            });

            wasInitialized = true;
        }
    }
}
