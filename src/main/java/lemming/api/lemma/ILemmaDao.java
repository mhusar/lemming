package lemming.api.lemma;

import lemming.api.data.IDao;

import java.util.List;

/**
 * Defines a lemma DAO by extending interface IDao.
 */
public interface ILemmaDao extends IDao<Lemma> {
    /**
     * Returns matching lemma for a given name.
     *
     * @param name name of a lemma
     * @return A matching lemma, or null.
     */
    Lemma findByName(String name);

    /**
     * Returns a list of matching lemmata for a given substring.
     *
     * @param substring substring of a lemma name
     * @return A list of matching lemmata.
     */
    List<Lemma> findByNameStart(String substring);
}
