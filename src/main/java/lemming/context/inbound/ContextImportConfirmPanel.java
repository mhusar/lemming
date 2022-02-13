package lemming.context.inbound;

import lemming.ui.panel.ModalMessagePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.StringResourceModel;

public class ContextImportConfirmPanel extends ModalMessagePanel {

    public ContextImportConfirmPanel() {
        super("importConfirmPanel", DialogType.YES_NO);
    }

    @Override
    protected String getTitleString() {
        return getString("Action.reallyImport");
    }

    @Override
    protected StringResourceModel getMessageModel() {
        return new StringResourceModel("ContextImportConfirmPanel.message");
    }

    @Override
    protected String getConfirmationString() {
        return getString("Action.reallyImport");
    }

    @Override
    protected String getAjaxIndicatorMarkupId() {
        return "indicatorOverlayPanel";
    }

    @Override
    protected void onCancel() {
    }

    @Override
    protected void onConfirm(AjaxRequestTarget target) {
        new InboundContextPackageDao().importContexts((InboundContextPackage) getDefaultModelObject());
        InboundContextPackagePanel panel = (InboundContextPackagePanel) getPage().get("contextPackagePanel");
        target.add(panel);
    }
}
