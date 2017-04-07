package lemming.user;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

import java.util.Locale;

/**
 * A text field converting a user’s real name to a user object.
 */
public class UserTextField extends TextField<User> {
    /**
     * Creates a user text field.
     *
     * @param id ID of the text field
     */
    @SuppressWarnings("SameParameterValue")
    public UserTextField(String id) {
        super(id);
    }

    /**
     * Returns a converter for user objects.
     * 
     * @param type class type of converted object
     * @return A converter for user objects.
     */
    @Override
    public <C> IConverter<C> getConverter(Class<C> type) {
        return new IConverter<C>() {
            /**
             * Converts an name string to a user object.
             * 
             * @param name name that is converted
             * @param locale locale used to convert the name
             * @return The converted object, or null.
             */
            @Override
            @SuppressWarnings("unchecked")
            public C convertToObject(String name, Locale locale) throws ConversionException {
                UserDao userDao = new UserDao();
                User user = userDao.findByRealName(name);

                if (user != null) {
                    return (C) user;
                } else {
                    return null;
                }
            }

            /**
             * Converts a user object to an name string.
             * 
             * @param user
             *            user that is converted
             * @param locale
             *            locale used to convert the user
             * @return The converted name, or null.
             */
            @Override
            public String convertToString(C user, Locale locale) {
                if (user instanceof User) {
                    return ((User) user).getRealName();
                }

                return null;
            }
        };
    }
}
