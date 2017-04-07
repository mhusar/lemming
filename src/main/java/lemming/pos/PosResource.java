package lemming.pos;

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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A resource for part of speech data.
 */
@Path("pos")
@RolesAllowed({"STUDENT","USER","ADMIN"})
public class PosResource {
    /**
     * Returns a chunked JSON response.
     *
     * @return A JSON response.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            StatelessSession session = entityManager.unwrap(Session.class).getSessionFactory().openStatelessSession();
            transaction = session.beginTransaction();
            Query query = session.createQuery("FROM Pos ORDER BY name");
            query.setReadOnly(true).setCacheable(false).setFetchSize(Integer.MIN_VALUE);
            ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);
            StreamingOutput streamingOutput = outputStream -> {
                JsonGenerator jsonGenerator = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, true)
                        .enable(SerializationFeature.INDENT_OUTPUT)
                        .getFactory().createGenerator(outputStream, JsonEncoding.UTF8);
                jsonGenerator.writeStartArray();

                while (results.next()) {
                    jsonGenerator.writeObject((Pos) results.get(0));
                    jsonGenerator.flush();
                }

                jsonGenerator.writeEndArray();
                jsonGenerator.flush();
                jsonGenerator.close();
                results.close();
                session.getTransaction().commit();
            };

            return Response.ok(streamingOutput).type(MediaType.APPLICATION_JSON)
                    .header("Content-Disposition", "attachment; filename=\"pos.json\"").build();
        } catch (RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        } finally {
            entityManager.close();
        }
    }
}
