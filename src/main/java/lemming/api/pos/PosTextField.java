package lemming.api.pos;

import java.util.Locale;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.hibernate.LazyInitializationException;

/**
 * A text field converting pos names to parts of speech.
 */
public class PosTextField extends TextField<Pos> {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a part of speech text field.
     * 
     * @param id
     *            ID of the text field
     */
    public PosTextField(String id) {
        super(id);
    }

    /**
     * Creates a part of speech text field.
     * 
     * @param id
     *            ID of the text field
     * @param model
     *            data model of the text field
     */
    public PosTextField(String id, IModel<Pos> model) {
        super(id, model);
    }

    /**
     * Returns a converter for part of speech objects.
     * 
     * @param type
     *            class type of converted object
     * @return A converter for part of speech objects.
     */
    @Override
    public <C> IConverter<C> getConverter(Class<C> type) {
        return new IConverter<C>() {
            /**
             * Determines if a deserialized file is compatible with this class.
             */
            private static final long serialVersionUID = 1L;

            /**
             * Converts an name string to a part of speech object.
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
                PosDao posDao = new PosDao();
                return (C) posDao.findByName(name);
            }

            /**
             * Converts a part of speech object to an name string.
             * 
             * @param pos
             *            part of speech that is converted
             * @param locale
             *            locale used to convert the part of speech
             * @return The converted name.
             */
            @Override
            public String convertToString(C pos, Locale locale) {
                try {
                    return ((Pos) pos).getName();
                } catch (LazyInitializationException e) {
                    Pos castedPos = (Pos) pos;

                    new PosDao().refresh(castedPos);
                    return castedPos.getName();
                }
            }
        };
    }
}
