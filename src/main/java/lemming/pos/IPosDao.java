package lemming.pos;

import lemming.data.IDao;
import lemming.data.Source;

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

    /**
     * Returns a list of matching parts of speech for a given source.
     *
     * @param source source of data
     * @return A list of matching parts of speech.
     */
    List<Pos> findBySource(Source.PosType source);
}
