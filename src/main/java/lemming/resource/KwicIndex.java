package lemming.resource;

import lemming.context.Context;
import lemming.context.ContextType;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A helper class with Java objects to build a KWIC item with JAXB or Velocity.
 */
public abstract class KwicIndex {
    /**
     * Sublist of element kwiclist.
     */
    @SuppressWarnings("unused")
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
         * Creates a sublist object. Needed for JAXB.
         */
        public SubList() {
        }

        /**
         * Creates a sublist.
         *
         * @param key key attribute
         */
        public SubList(String key) {
            this.key = key;
            items = new ArrayList<>();
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
         * Returns the key attribute of a sublist element.
         *
         * @return A key attribute as string.
         */
        public String getKey() {
            return key;
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
    @SuppressWarnings("unused")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Item {
        /**
         * Following attribute of an item.
         */
        @XmlAttribute(required = true)
        private String following;

        /**
         * Lemma attribute of an item.
         */
        @XmlAttribute
        private String lemma;

        /**
         * Location attribute of an item.
         */
        @XmlAttribute(required = true)
        private String location;

        /**
         * Pos attribute of an item.
         */
        @XmlAttribute
        private String pos;

        /**
         * Preceding attribute of an item.
         */
        @XmlAttribute(required = true)
        private String preceding;

        /**
         * Type attribute of an item.
         */
        @XmlAttribute(required = true)
        private String type;

        /**
         * End punctuation tag of an item.
         */
        @XmlElement(name = "punctuation")
        private Punctuation initPunctuation;

        /**
         * End punctuation tag of an item as string.
         */
        @XmlTransient
        private String initPunctuationString;

        /**
         * Keyword value of an item.
         */
        @XmlElement(name = "string")
        private Keyword keyword;

        /**
         * End punctuation tag of an item.
         */
        @XmlElement(name = "punctuation")
        private Punctuation endPunctuation;

        /**
         * End punctuation tag of an item as string.
         */
        @XmlTransient
        private String endPunctuationString;

        /**
         * Creates an item object.
         *
         * @param context a context
         */
        public Item(Context context) {
            following = context.getFollowing();
            keyword = new Keyword(context.getKeyword());
            location = context.getLocation();
            preceding = context.getPreceding();

            if (context.getInitPunctuation() != null) {
                initPunctuation = new Punctuation(context.getInitPunctuation(), "init");
                initPunctuationString = context.getInitPunctuation();
            }

            if (context.getEndPunctuation() != null) {
                endPunctuation = new Punctuation(context.getEndPunctuation(), "end");
                endPunctuationString = context.getEndPunctuation();
            }

            if (context.getLemma() != null) {
                lemma = context.getLemma().getName();
            }

            if (context.getPos() != null) {
                pos = context.getPos().getName();
            }

            if (context.getType().equals(ContextType.Type.RUBRIC)) {
                type = "rubric_item";
            } else if (context.getType().equals(ContextType.Type.SEGMENT)) {
                type = "seg_item";
            }
        }

        /**
         * Returns the following attribute of an item.
         *
         * @return An attribute as string.
         */
        public String getFollowing() {
            return following;
        }

        /**
         * Returns the lemma attribute of an item.
         *
         * @return An attribute as string.
         */
        public String getLemma() {
            return lemma;
        }

        /**
         * Returns the location attribute of an item.
         *
         * @return An attribute as string.
         */
        public String getLocation() {
            return location;
        }

        /**
         * Returns the pos attribute of an item.
         *
         * @return An attribute as string.
         */
        public String getPos() {
            return pos;
        }

        /**
         * Returns the preceding attribute of an item.
         *
         * @return An attribute as string.
         */
        public String getPreceding() {
            return preceding;
        }

        /**
         * Returns the type attribute of an item.
         *
         * @return An attribute as string.
         */
        public String getType() {
            return type;
        }

        /**
         * Returns the init punctuation tag of an item.
         *
         * @return A punctuation object.
         */
        public Punctuation getInitPunctuation() {
            return initPunctuation;
        }

        /**
         * Returns the init punctuation tag of an item as string.
         *
         * @return A string object.
         */
        public String getInitPunctuationString() {
            return initPunctuationString;
        }

        /**
         * Returns the keyword attribute of an item.
         *
         * @return A keyword object.
         */
        public Keyword getKeyword() {
            return keyword;
        }

        /**
         * Returns the end punctuation tag of an item.
         *
         * @return A punctuation object.
         */
        public Punctuation getEndPunctuation() {
            return endPunctuation;
        }

        /**
         * Returns the end punctuation tag of an item as string.
         *
         * @return A string object.
         */
        public String getEndPunctuationString() {
            return endPunctuationString;
        }
    }

    /**
     * Keyword of a context item.
     */
    @SuppressWarnings("unused")
    public static class Keyword {
        /**
         * Value of a keyword.
         */
        @XmlValue
        private String value;

        /**
         * Creates a keyword object.
         *
         * @param value value of a keyword
         */
        public Keyword(String value) {
            this.value = value;
        }

        /***
         * Returns the value of a keyword.
         *
         * @return A value as string.
         */
        public String getValue() {
            return value;
        }
    }

    /**
     * Punctuation of a context item.
     */
    @SuppressWarnings("unused")
    public static class Punctuation {
        /**
         * Type of punctuation.
         */
        @XmlAttribute(required = true)
        private String type;

        /**
         * Value of a punctuation item.
         */
        @XmlValue
        private String value;

        /**
         * Creates a punctuation object.
         *
         * @param value value of a punctuation
         * @param type  type of punctuation
         */
        public Punctuation(String value, String type) {
            this.type = type;
            this.value = value;
        }

        /**
         * Returns the type of a punctuation.
         *
         * @return An attribute as string.
         */
        public String getType() {
            return type;
        }

        /***
         * Returns the value of a punctuation.
         *
         * @return A value as string.
         */
        public String getValue() {
            return value;
        }
    }
}
