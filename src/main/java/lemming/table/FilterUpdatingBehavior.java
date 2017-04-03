package lemming.table;

import lemming.data.GenericDataProvider;
import org.apache.wicket.ajax.AjaxChannel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.ThrottlingSettings;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.time.Duration;

/**
 * Implementation of a form component updating behavior for a filter text field.
 */
public class FilterUpdatingBehavior extends AjaxFormComponentUpdatingBehavior {
    /**
     * The text field used as filter component.
     */
    TextField<String> textField;

    /**
     * Data table displaying filtered data.
     */
    GenericDataTable<?> dataTable;

    /**
     * Data provider which delivers data for the table.
     */
    GenericDataProvider<?> dataProvider;

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
    public FilterUpdatingBehavior(TextField<String> textField, GenericDataTable<?> dataTable,
                                  GenericDataProvider<?> dataProvider) {
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
