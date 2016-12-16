package lemming.api.pos;

import lemming.api.data.IDao;

import java.util.List;

/**
 * Defines a part of speech DAO by extending interface IDao.
 */
public interface IPosDao extends IDao<Pos> {
    /**
     * Returns a list of matching parts of speech for a given substring.
     *
     * @param substring substring of a part of speech name
     * @return A list of matching parts of speech.
     */
    List<Pos> findByName(String substring);
}
