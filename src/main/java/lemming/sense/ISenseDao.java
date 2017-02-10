package lemming.sense;

import lemming.data.IDao;

import java.util.List;

/**
 * Defines a sense DAO by extending interface IDao.
 */
public interface ISenseDao extends IDao<Sense> {
    /**
     * Returns a matching sense for a meaning.
     *
     * @param meaning meaning of a sense
     * @return A matching sense, or null.
     */
    Sense findByMeaning(String meaning);

    /**
     * Find sense tree root nodes by lemma.
     *
     * @param lemma lemma of a sense
     * @return A list of senses.
     */
    List<Sense> findRootNodes(lemming.lemma.Lemma lemma);
}
