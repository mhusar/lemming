package lemming.context;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lemming.data.EntityManagerListener;
import lemming.resource.KwicIndex;
import org.glassfish.hk2.utilities.general.IndentingXMLStreamWriter;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A resource for context data.
 */
@Path("contexts")
@RolesAllowed({"STUDENT","USER","ADMIN"})
public class ContextResource {
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
            final EntityTransaction finalTransaction = session.beginTransaction();
            transaction = finalTransaction;
            Query contextQuery = session.createQuery("FROM Context ORDER BY keyword, location");
            contextQuery.setReadOnly(true).setCacheable(false).setFetchSize(Integer.MIN_VALUE);
            ScrollableResults results = contextQuery.scroll(ScrollMode.FORWARD_ONLY);
            StreamingOutput streamingOutput = new StreamingOutput() {
                @Override
                public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                    JsonGenerator jsonGenerator = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, true)
                            .getFactory().createGenerator(outputStream, JsonEncoding.UTF8);
                    jsonGenerator.writeStartArray();

                    while (results.next()) {
                        Context context = (Context) results.get(0);
                        jsonGenerator.writeObject(context);
                    }

                    jsonGenerator.writeEndArray();
                    jsonGenerator.flush();
                    jsonGenerator.close();
                    results.close();
                    finalTransaction.commit();
                }
            };

            return Response.ok(streamingOutput).type(MediaType.APPLICATION_JSON)
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
            final EntityTransaction finalTransaction = session.beginTransaction();
            transaction = finalTransaction;
            Query contextQuery = session.createQuery("FROM Context ORDER BY keyword, location");
            contextQuery.setReadOnly(true).setCacheable(false).setFetchSize(Integer.MIN_VALUE);
            ScrollableResults results = contextQuery.scroll(ScrollMode.FORWARD_ONLY);

            StreamingOutput streamingOutput = new StreamingOutput() {
                @Override
                public void write(OutputStream outputStream) throws IOException, RuntimeException {
                    try {
                        Marshaller marshaller = JAXBContext.newInstance(KwicIndex.SubList.class).createMarshaller();
                        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                        XMLStreamWriter streamWriter = XMLOutputFactory.newInstance()
                                .createXMLStreamWriter(outputStream);
                        IndentingXMLStreamWriter indentingStreamWriter = new IndentingXMLStreamWriter(streamWriter);
                        KwicIndex.SubList subList = null;
                        String lastKeyword = null;

                        streamWriter.writeStartDocument("UTF-8", "1.0");
                        streamWriter.writeStartElement("kwiclist");
                        while (results.next()) {
                            Context context = (Context) results.get(0);
                            String keyword = context.getKeyword();

                            if (keyword.equals(lastKeyword)) {
                                subList.addContext(context);
                            } else {
                                if (subList instanceof KwicIndex.SubList) {
                                    marshaller.marshal(subList, indentingStreamWriter);
                                    indentingStreamWriter.flush();
                                }

                                lastKeyword = keyword;
                                subList = new KwicIndex.SubList(keyword);
                                subList.addContext(context);
                            }
                        }

                        if (subList instanceof KwicIndex.SubList) {
                            marshaller.marshal(subList, indentingStreamWriter);
                        }

                        indentingStreamWriter.writeEndDocument();
                        indentingStreamWriter.flush();
                        results.close();
                        finalTransaction.commit();
                    } catch (JAXBException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    } catch (XMLStreamException e) {
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
