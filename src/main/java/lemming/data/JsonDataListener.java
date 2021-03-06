package lemming.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import lemming.lemma.Lemma;
import lemming.lemma.LemmaDao;
import lemming.pos.Pos;
import lemming.pos.PosDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Listener for receiving notification events about ServletContext lifecycle changes.
 */
@WebListener
public class JsonDataListener implements ServletContextListener {
    /**
     * A logger named corresponding to this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(JsonDataListener.class);

    /**
     * Persists provided lemma data from TL and pos data from DEAF.
     *
     * @param servletContextEvent event containing the ServletContext that is being initialized
     */
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ObjectMapper mapper = new ObjectMapper();
        ServletContext context = servletContextEvent.getServletContext();
        LemmaDao lemmaDao = new LemmaDao();
        PosDao posDao = new PosDao();

        if (lemmaDao.findBySource(Source.LemmaType.TL).isEmpty()) {
            logger.info("Persisting lemma data");

            try {
                Lemma[] lemmaArray = mapper.readValue(context.getResourceAsStream("/WEB-INF/json/lemma.json"),
                        Lemma[].class);

                for (int from = 0; from < lemmaArray.length; from += 50) {
                    int to = (from + 50 > lemmaArray.length) ? lemmaArray.length : from + 50;
                    lemmaDao.batchPersist(Arrays.asList(Arrays.copyOfRange(lemmaArray, from, to)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<Lemma> lemmaList = lemmaDao.findResolvableLemmata();

            for (int from = 0; from < lemmaList.size(); from += 50) {
                int to = (from + 50 > lemmaList.size()) ? lemmaList.size() : from + 50;
                List<Lemma> subList = lemmaList.subList(from, to);
                Boolean result = lemmaDao.batchResolve(subList);

                if (!result) {
                    logger.error("At least one replacement lemma doesn’t exist!");
                }
            }
        }

        if (posDao.findBySource(Source.PosType.DEAF).isEmpty()) {
            logger.info("Persisting pos data");

            try {
                Pos[] posArray = mapper.readValue(context.getResourceAsStream("/WEB-INF/json/pos.json"),
                        Pos[].class);

                for (Pos pos : posArray) {
                    posDao.persist(pos);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Does nothing.
     *
     * @param servletContextEvent event containing the ServletContext that is being destroyed
     */
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
