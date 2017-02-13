package lemming.resource;

import lemming.context.Context;
import lemming.context.ContextType;
import lemming.lemma.Lemma;
import lemming.pos.Pos;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A helper class with Java objects to build a KWIC index with JAXB.
 */
public abstract class KwicIndex {
    /**
     * Sublist of element kwiclist.
     */
    @XmlRootElement(name = "sublist")
    public static class SubList {
        /**
         * Key attribute of a sublist element.
         */
        @XmlAttribute
        private String key;

        /**
         * Items of a sublist.
         */
        @XmlElement(name = "item")
        private List<Item> items;

        /**
         * Creates a sublist. Needed for JAXB.
         */
        public SubList() {}

        /**
         * Creates a sublist.
         *
         * @param key key attribute
         */
        public SubList(String key) {
            this.key = key;
            items = new ArrayList<Item>();
        }

        /**
         * Add a context as item to the sublist.
         *
         * @param context a context
         */
        public void addContext(Context context) {
            getItems().add(new Item(context));
        }

        /**
         * Returns items of a sublist.
         *
         * @return A list of items.
         */
        public List<Item> getItems() {
            return items;
        }
    }

    /**
     * Item of a sublist element.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Item {
        /**
         * Creates an item.
         *
         * @param context a context
         */
        public Item(Context context) {
            following = context.getFollowing();
            keyword = context.getKeyword();
            location = context.getLocation();
            preceding = context.getPreceding();

            if (context.getLemma() instanceof Lemma) {
                lemma = context.getLemma().getName();
            }

            if (context.getPos() instanceof Pos) {
                pos = context.getPos().getName();
            }

            if (context.getType().equals(ContextType.Type.RUBRIC)) {
                type = "rubric_item";
            } else if (context.getType().equals(ContextType.Type.SEGMENT)) {
                type = "seg_item";
            }
        }

        /**
         * Following attribute of an index.
         */
        @XmlAttribute(required = true)
        private String following;

        /**
         * Keyword value of an index.
         */
        @XmlValue
        private String keyword;

        /**
         * Lemma attribute of an index.
         */
        @XmlAttribute
        private String lemma;

        /**
         * Location attribute of an index.
         */
        @XmlAttribute(required = true)
        private String location;

        /**
         * Pos attribute of an index.
         */
        @XmlAttribute
        private String pos;

        /**
         * Preceding attribute of an index.
         */
        @XmlAttribute(required = true)
        private String preceding;

        /**
         * Type attribute of an index.
         */
        @XmlAttribute(required = true)
        private String type;
    }
}
