package lemming.api.lemma;

import lemming.api.data.IDao;
import lemming.api.data.Source;

import java.util.List;

/**
 * Defines a lemma DAO by extending interface IDao.
 */
public interface ILemmaDao extends IDao<Lemma> {
    /**
     * Makes multiple lemma instances managed and persistent.
     *
     * @param lemmas lemma instances
     */
    void batchPersist(List<Lemma> lemmas);

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

    /**
     * Returns a list of matching lemmata for a given source.
     *
     * @param source source of data
     * @return A list of matching lemmata.
     */
    List<Lemma> findBySource(Source.LemmaType source);

    /**
     * Returns a list of lemmata with resolvable replacement string.
     *
     * @return A list of matching lemmata.
     */
    List<Lemma> findResolvableLemmata();

    /**
     * Resolves a replacement lemma with a replacement string.
     *
     * @param lemmas processed lemma list
     * @return True if all lemmata had a replacement.
     */
    Boolean batchResolve(List<Lemma> lemmas);
}
