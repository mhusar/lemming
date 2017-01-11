package lemming.api.character;

import lemming.api.data.IDao;

/**
 * Defines a special character DAO by extending interface IDao.
 */
public interface ICharacterDao extends IDao<Character> {
    /**
     * Returns the matching character for a given character string.
     * 
     * @param character
     *            the character string of a character
     * @return The matching character or null.
     */
    Character findByCharacter(String character);
}
