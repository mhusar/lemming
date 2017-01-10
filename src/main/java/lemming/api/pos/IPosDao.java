package lemming.api.pos;

import lemming.api.data.IDao;

import java.util.List;

/**
 * Defines a part of speech DAO by extending interface IDao.
 */
public interface IPosDao extends IDao<Pos> {
    /**
     * Returns a matching part of speech for a given name.
     *
     * @param name name of a part of speech
     * @return A matching part of speech, or null.
     */
    Pos findByName(String name);

    /**
     * Returns a list of matching parts of speech for a given substring.
     *
     * @param substring substring of a part of speech name
     * @return A list of matching parts of speech.
     */
    List<Pos> findByNameStart(String substring);
}
