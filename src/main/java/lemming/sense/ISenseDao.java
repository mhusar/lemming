package lemming.sense;

import lemming.data.IDao;
import lemming.lemma.Lemma;

import java.util.List;

/**
 * Defines a sense DAO by extending interface IDao.
 */
public interface ISenseDao extends IDao<Sense> {
    /**
     * Returns a list of matching senses for a given lemma.
     *
     * @param lemma a lemma
     * @return A list of matching senses.
     */
    List<Sense> findByLemma(Lemma lemma);

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
    List<Sense> findRootNodes(Lemma lemma);

    /**
     * Checks if a sense has any child senses.
     *
     * @param sense a sense
     * @return True if a sense has child senses, false otherwise.
     */
    Boolean hasChildSenses(Sense sense);
}
