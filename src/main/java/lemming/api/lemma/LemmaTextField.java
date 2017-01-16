package lemming.api.lemma;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.hibernate.LazyInitializationException;

import java.util.Locale;

/**
 * A text field converting lemma names to lemmata.
 */
public class LemmaTextField extends TextField<Lemma> {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a lemma text field.
     * 
     * @param id
     *            ID of the text field
     */
    public LemmaTextField(String id) {
        super(id);
    }

    /**
     * Creates a lemma text field.
     * 
     * @param id
     *            ID of the text field
     * @param model
     *            data model of the text field
     */
    public LemmaTextField(String id, IModel<Lemma> model) {
        super(id, model);
    }

    /**
     * Returns a converter for lemma objects.
     * 
     * @param type
     *            class type of converted object
     * @return A converter for lemma objects.
     */
    @Override
    public <C> IConverter<C> getConverter(Class<C> type) {
        return new IConverter<C>() {
            /**
             * Determines if a deserialized file is compatible with this class.
             */
            private static final long serialVersionUID = 1L;

            /**
             * Converts an name string to a lemma object.
             * 
             * @param name
             *            name that is converted
             * @param locale
             *            locale used to convert the name
             * @return The converted object.
             * @throws ConversionException
             */
            @Override
            @SuppressWarnings("unchecked")
            public C convertToObject(String name, Locale locale) throws ConversionException {
                LemmaDao lemmaDao = new LemmaDao();
                return (C) lemmaDao.findByName(name);
            }

            /**
             * Converts a lemma object to an name string.
             * 
             * @param lemma
             *            lemma that is converted
             * @param locale
             *            locale used to convert the lemma
             * @return The converted name.
             */
            @Override
            public String convertToString(C lemma, Locale locale) {
                try {
                    return ((Lemma) lemma).getName();
                } catch (LazyInitializationException e) {
                    Lemma castedLemma = (Lemma) lemma;

                    new LemmaDao().refresh(castedLemma);
                    return castedLemma.getName();
                }
            }
        };
    }
}
