package lemming.lemma;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A resource for lemma data.
 */
@Path("lemmas")
@RolesAllowed({"STUDENT","USER","ADMIN"})
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
        EntityManager entityManager2 = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            StatelessSession session = entityManager1.unwrap(Session.class).getSessionFactory().openStatelessSession();
            final EntityTransaction finalTransaction = session.beginTransaction();
            transaction = finalTransaction;
            Query query = session.createQuery("SELECT l.id FROM Lemma l WHERE l.replacement IS NULL ORDER BY l.name");
            query.setReadOnly(true).setCacheable(false).setFetchSize(Integer.MIN_VALUE);
            ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);
            StreamingOutput streamingOutput = new StreamingOutput() {
                @Override
                public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                    JsonGenerator jsonGenerator = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, true)
                            .getFactory().createGenerator(outputStream, JsonEncoding.UTF8);
                    jsonGenerator.writeStartArray();

                    while (results.next()) {
                        Lemma lemma = entityManager2.find(Lemma.class, results.get(0));

                        if (lemma instanceof Lemma) {
                            jsonGenerator.writeObject(lemma);
                        }

                        entityManager2.clear();
                    }

                    jsonGenerator.writeEndArray();
                    jsonGenerator.flush();
                    jsonGenerator.close();
                    entityManager2.close();
                    results.close();
                    finalTransaction.commit();
                }
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
