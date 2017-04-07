package lemming.pos;

import java.util.Locale;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

/**
 * A text field converting pos names to parts of speech.
 */
public class PosTextField extends TextField<Pos> {
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
             * Converts an name string to a part of speech object.
             * 
             * @param name
             *            name that is converted
             * @param locale
             *            locale used to convert the name
             * @return The converted object, or null.
             */
            @Override
            @SuppressWarnings("unchecked")
            public C convertToObject(String name, Locale locale) throws ConversionException {
                PosDao posDao = new PosDao();
                Pos pos = posDao.findByName(name);

                if (pos != null) {
                    return (C) pos;
                } else {
                    return null;
                }
            }

            /**
             * Converts a part of speech object to an name string.
             * 
             * @param pos
             *            part of speech that is converted
             * @param locale
             *            locale used to convert the part of speech
             * @return The converted name, or null.
             */
            @Override
            public String convertToString(C pos, Locale locale) {
                if (pos instanceof Pos) {
                    return new PosDao().getPosName((Pos) pos);
                }

                return null;
            }
        };
    }
}
