package lemming.api.character;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import lemming.api.ui.AjaxView;

/**
 * An asynchronously refreshing repeating view for special characters.
 */
public class CharacterAjaxView extends AjaxView<Character> {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an Ajax view for special characters.
     *
     * @param id ID of the view
     */
    public CharacterAjaxView(String id) {
        super(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Iterator<IModel<Character>> getItemModels() {
        List<IModel<Character>> characterModels = new ArrayList<IModel<Character>>();
        Iterator<Character> characterIterator = new CharacterDao().getAll().iterator();

        while (characterIterator.hasNext()) {
            characterModels.add(new Model<Character>(characterIterator.next()));
        }

        return characterModels.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Item<Character> getNewItem(String id, int index, IModel<Character> model, Boolean isSelected) {
        Item<Character> item = new Item<Character>(id, index, model);
        Label label = new Label("label", model.getObject().getCharacter());

        if (isSelected) {
            item.add(AttributeModifier.append("class", "list-group-item active"));
        } else {
            item.add(AttributeModifier.append("class", "list-group-item"));
        }

        item.add(label);
        return item;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getRefreshNoItemContainerJavaScript(String id, String parentId, Boolean isVisible) {
        if (isVisible()) {
            String cssClass = "list-group-item";
            String javaScript = String.format("jQuery(\"#%s\").remove(); "
                            + "var item = jQuery(\"<div></div>\"); "
                            + "item.attr(\"id\", \"%s\").attr(\"class\", \"%s\"); "
                            + "jQuery(\"#%s .list-group\").prepend(item);", id, id,
                    cssClass, parentId);
            return javaScript;
        } else {
            String javaScript = String.format("jQuery(\"#%s\").remove();", id);
            return javaScript;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getRefreshItemJavaScript(String id, int index, IModel<Character> model, Boolean isSelected) {
        String javaScript = String.format("var item = jQuery(\"#%s\"); " + "jQuery(\"%s\", item).text(\"%s\"); ",
                id, "span", model.getObject().getCharacter());

        if (isSelected) {
            javaScript += String.format("item.addClass(\"%s\");", "active");
        } else {
            javaScript += String.format("item.removeClass(\"%s\");", "active");
        }

        return javaScript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getAppendItemJavaScript(String id, String parentId, int index, IModel<Character> model,
                                             Boolean isSelected) {
        String cssClass = (isSelected) ? "list-group-item active" : "list-group-item";
        String javaScript = String.format(
                "var item = jQuery(\"<div></div>\"); "
                        + "item.attr(\"id\", \"%s\").addClass(\"%s\"); "
                        + "item.append("
                        + "jQuery(\"<span></span>\").text(\"%s\")); "
                        + "jQuery(\"#%s .list-group\").append(item);", id,
                cssClass, model.getObject().getCharacter(), parentId);

        return javaScript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getRemoveItemJavaScript(String id) {
        String javaScript = String.format("var item = jQuery(\"#%s\"); item.remove();", id);
        return javaScript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onItemClick(AjaxRequestTarget target, IModel<Character> model) {
        CharacterViewPanel panel = findParent(CharacterViewPanel.class);
        panel.onItemClick(target, model);
    }
}
