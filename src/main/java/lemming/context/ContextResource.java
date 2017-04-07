package lemming.context;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lemming.data.EntityManagerListener;
import lemming.resource.KwicIndex;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.io.VelocityWriter;
import org.glassfish.hk2.utilities.general.IndentingXMLStreamWriter;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.query.Query;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Properties;

/**
 * A resource for context data.
 */
@Path("contexts")
@RolesAllowed({"STUDENT","USER","ADMIN"})
public class ContextResource {
    /**
     * The servlet context.
     */
    @javax.ws.rs.core.Context
    ServletContext context;

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
            Query query = session.createQuery("SELECT DISTINCT c.keyword FROM Context c ORDER BY c.keyword");
            query.setReadOnly(true).setCacheable(false).setFetchSize(Integer.MIN_VALUE);
            ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);
            StreamingOutput streamingOutput = new StreamingOutput() {
                @Override
                public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                    JsonGenerator jsonGenerator = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, true)
                            .enable(SerializationFeature.INDENT_OUTPUT)
                            .getFactory().createGenerator(outputStream, JsonEncoding.UTF8);
                    jsonGenerator.writeStartArray();

                    while (results.next()) {
                        writeJsonResult(jsonGenerator, results.getString(0));
                    }

                    jsonGenerator.writeEndArray();
                    jsonGenerator.flush();
                    jsonGenerator.close();
                    results.close();
                    session.getTransaction().commit();
                }
            };

            return Response.ok(streamingOutput).type("text/json")
                .header("Content-Disposition", "attachment; filename=\"contexts.json\"").build();
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

    /**
     * Writes context items per JSON generator.
     *
     * @param jsonGenerator a JSON generator
     * @param keyword a context keyword
     * @throws IOException
     */
    private void writeJsonResult(JsonGenerator jsonGenerator, String keyword) throws IOException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            for (Context context : entityManager
                    .createQuery("SELECT c FROM Context c LEFT JOIN FETCH c.lemma l LEFT JOIN FETCH l.pos " +
                            "LEFT JOIN FETCH c.pos WHERE c.keyword = :keyword ORDER BY c.location", Context.class)
                    .setParameter("keyword", keyword).getResultList()) {
                jsonGenerator.writeObject(context);
            }

            jsonGenerator.flush();
            transaction.commit();
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        } finally {
            entityManager.close();
        }
    }

    /**
     * Returns a chunked XML response as KWIC index.
     *
     * @return A XML response.
     */
    @GET
    @Path("xml")
    @Produces(MediaType.TEXT_XML)
    public Response getXml() {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            StatelessSession session = entityManager.unwrap(Session.class).getSessionFactory().openStatelessSession();
            transaction = session.beginTransaction();
            ScrollableResults results = session
                    .createQuery("SELECT DISTINCT c.keyword FROM Context c ORDER BY c.keyword")
                    .setReadOnly(true).setCacheable(false).setFetchSize(Integer.MIN_VALUE)
                    .scroll(ScrollMode.FORWARD_ONLY);
            StreamingOutput streamingOutput = new StreamingOutput() {
                @Override
                public void write(OutputStream outputStream) throws IOException {
                    Properties properties = new Properties();
                    properties.load(context.getResourceAsStream("/WEB-INF/classes/velocity.properties"));
                    VelocityEngine velocityEngine = new VelocityEngine(properties);
                    VelocityWriter velocityWriter = new VelocityWriter(new OutputStreamWriter(outputStream));
                    Template template = velocityEngine.getTemplate("lemming/resource/templates/kwicindex.vm");

                    velocityEngine.init();
                    velocityWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                    velocityWriter.write("<kwiclist>\n");

                    while (results.next()) {
                        VelocityContext velocityContext = writeXmlResult(results.getString(0));
                        template.merge(velocityContext, velocityWriter);
                        velocityWriter.flush();
                    }

                    velocityWriter.write("</kwiclist>\n");
                    velocityWriter.flush();
                    results.close();
                    session.getTransaction().commit();
                }
            };

            return Response.ok(streamingOutput).type(MediaType.TEXT_XML)
                    .header("Content-Disposition", "attachment; filename=\"contexts.xml\"").build();
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

    /**
     * Writes context items to a velocity context as XML.
     *
     * @param keyword a context keyword
     * @return A velocity context.
     */
    private VelocityContext writeXmlResult(String keyword) {
        VelocityContext velocityContext = new VelocityContext();
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Iterator<Context> iterator = entityManager.createQuery("SELECT c FROM Context c LEFT JOIN FETCH c.lemma " +
                            "LEFT JOIN FETCH c.pos WHERE c.keyword = :keyword ORDER BY c.location", Context.class)
                    .setParameter("keyword", keyword).getResultList().iterator();
            KwicIndex.SubList subList = new KwicIndex.SubList(keyword);

            while (iterator.hasNext()) {
                subList.addContext(iterator.next());
            }

            transaction.commit();
            velocityContext.put("sublist", subList);
        } catch (RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        } finally {
            entityManager.close();
        }

        return velocityContext;
    }

    /**
     * Returns a chunked XML response as KWIC index.
     *
     * @return A XML response.
     */
    @GET
    @Path("xml2")
    @Produces(MediaType.TEXT_XML)
    public Response getXml2() {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            StatelessSession session = entityManager.unwrap(Session.class).getSessionFactory().openStatelessSession();
            transaction = session.beginTransaction();
            ScrollableResults results = session
                    .createQuery("SELECT DISTINCT c.keyword FROM Context c ORDER BY c.keyword")
                    .setReadOnly(true).setCacheable(false).setFetchSize(Integer.MIN_VALUE)
                    .scroll(ScrollMode.FORWARD_ONLY);
            StreamingOutput streamingOutput = new StreamingOutput() {
                @Override
                public void write(OutputStream outputStream) throws IOException, RuntimeException {
                    try {
                        Marshaller marshaller = JAXBContext.newInstance(KwicIndex.SubList.class).createMarshaller();
                        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                        XMLStreamWriter streamWriter = XMLOutputFactory.newInstance()
                                .createXMLStreamWriter(outputStream);
                        IndentingXMLStreamWriter indentingStreamWriter = new IndentingXMLStreamWriter(streamWriter);

                        streamWriter.writeStartDocument("UTF-8", "1.0");
                        streamWriter.writeCharacters("\n");
                        streamWriter.writeStartElement("kwiclist");
                        streamWriter.writeCharacters("\n");

                        while (results.next()) {
                            String keyword = results.getString(0);
                            EntityManager entityManager2 = EntityManagerListener.createEntityManager();
                            Iterator<Context> iterator = entityManager2.createQuery("SELECT c FROM Context c " +
                                            "LEFT JOIN FETCH c.lemma LEFT JOIN FETCH c.pos " +
                                            "WHERE c.keyword = :keyword ORDER BY c.location", Context.class)
                                    .setParameter("keyword", keyword).getResultList().iterator();
                            KwicIndex.SubList subList = new KwicIndex.SubList(keyword);

                            while (iterator.hasNext()) {
                                subList.addContext(iterator.next());
                            }

                            marshaller.marshal(subList, indentingStreamWriter);
                            indentingStreamWriter.flush();
                            entityManager2.close();
                        }

                        streamWriter.writeCharacters("\n");
                        indentingStreamWriter.writeEndDocument();
                        streamWriter.writeCharacters("\n");
                        indentingStreamWriter.flush();
                        results.close();
                        session.getTransaction().commit();
                    } catch (JAXBException | XMLStreamException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
            };

            return Response.ok(streamingOutput).type(MediaType.TEXT_XML)
                    .header("Content-Disposition", "attachment; filename=\"contexts.xml\"").build();
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
