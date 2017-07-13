package lemming.lemma;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lemming.data.EntityManagerListener;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.query.Query;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.util.ArrayList;
import java.util.List;

/**
 * A resource for lemma data.
 */
@Path("lemmas")
@RolesAllowed({"STUDENT", "USER", "ADMIN"})
public class LemmaResource {
    /**
     * Returns a chunked JSON response.
     *
     * @return A JSON response.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        EntityManager entityManager1 = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            StatelessSession session = entityManager1.unwrap(Session.class).getSessionFactory().openStatelessSession();
            transaction = session.beginTransaction();
            Query query = session.createQuery("SELECT l.id FROM Lemma l ORDER BY l.name");
            query.setReadOnly(true).setCacheable(false).setFetchSize(Integer.MIN_VALUE);
            ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);
            StreamingOutput streamingOutput = outputStream -> {
                JsonGenerator jsonGenerator = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, true)
                        .enable(SerializationFeature.INDENT_OUTPUT)
                        .getFactory().createGenerator(outputStream, JsonEncoding.UTF8);
                jsonGenerator.writeStartArray();
                EntityManager entityManager2 = EntityManagerListener.createEntityManager();
                List<Integer> idList = new ArrayList<>(1000);

                while (results.next()) {
                    idList.add(results.getInteger(0));

                    if (idList.size() == 1000) {
                        List<Lemma> lemmaList = entityManager2
                                .createQuery("SELECT l FROM Lemma l LEFT JOIN FETCH l.replacement " +
                                        "LEFT JOIN FETCH l.pos WHERE l.replacement IS NULL " +
                                        "AND l.id IN (:ids) ORDER BY l.name", Lemma.class)
                                .setParameter("ids", idList).getResultList();

                        for (Lemma lemma : lemmaList) {
                            jsonGenerator.writeObject(lemma);
                            jsonGenerator.flush();
                        }

                        idList.clear();
                        entityManager2.clear();
                    }
                }

                if (!idList.isEmpty()) {
                    List<Lemma> lemmaList = entityManager2
                            .createQuery("SELECT l FROM Lemma l LEFT JOIN FETCH l.replacement " +
                                    "LEFT JOIN FETCH l.pos WHERE l.replacement IS NULL " +
                                    "AND l.id IN (:ids) ORDER BY l.name", Lemma.class)
                            .setParameter("ids", idList).getResultList();

                    for (Lemma lemma : lemmaList) {
                        jsonGenerator.writeObject(lemma);
                        jsonGenerator.flush();
                    }
                }

                jsonGenerator.writeEndArray();
                jsonGenerator.flush();
                jsonGenerator.close();
                entityManager2.close();
                results.close();
                session.getTransaction().commit();
            };

            return Response.ok(streamingOutput).type(MediaType.APPLICATION_JSON)
                    .header("Content-Disposition", "attachment; filename=\"lemmata.json\"").build();
        } catch (RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        } finally {
            entityManager1.close();
        }
    }
}
