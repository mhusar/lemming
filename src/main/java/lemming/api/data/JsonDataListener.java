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

@WebListener
public class JsonDataListener implements ServletContextListener {

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
