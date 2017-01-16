package lemming.api.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import lemming.api.lemma.Lemma;
import lemming.api.lemma.LemmaDao;
import lemming.api.pos.Pos;
import lemming.api.pos.PosDao;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class JsonDataListener implements ServletContextListener {

    /**
     * A logger named corresponding to this class.
     */
    private static final Logger logger = Logger.getLogger(JsonDataListener.class.getName());

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ObjectMapper mapper = new ObjectMapper();
        ServletContext context = servletContextEvent.getServletContext();
        LemmaDao lemmaDao = new LemmaDao();
        PosDao posDao = new PosDao();

        if (lemmaDao.findBySource(Source.LemmaType.TL).isEmpty()) {
            try {
                Lemma[] lemmaArray = mapper.readValue(context.getResourceAsStream("/WEB-INF/json/lemma.json"),
                        Lemma[].class);

                for (Lemma lemma : lemmaArray) {
                    lemmaDao.persist(lemma);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<Lemma> lemmaList = lemmaDao.findBySource(Source.LemmaType.TL);

            for (Lemma lemma : lemmaList) {
                if (lemma.getReplacementString() instanceof String) {
                    Boolean result = lemmaDao.resolveReplacement(lemma);

                    if (result == false) {
                        logger.log(Level.SEVERE, "Replacement lemma doesn’t exist: " + lemma.getName() + " → "
                                + lemma.getReplacementString());
                    }
                }
            }
        }

        if (posDao.findBySource(Source.PosType.DEAF).isEmpty()) {
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

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
