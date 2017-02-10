package lemming.lemma;

import lemming.data.Source;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("lemmas")
public class LemmaResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        List<Lemma> list = new LemmaDao().getAll();
        return Response.ok(list).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") String id) {
        Lemma lemma = new LemmaDao().find(Integer.valueOf(id));

        if (lemma != null) {
            return Response.ok(lemma).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/name/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByName(@PathParam("name") String name) {
        List<Lemma> list = new LemmaDao().findByNameStart(name);
        return Response.ok(list).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(Lemma lemma) {
        if (lemma != null) {
            lemma.setSource(Source.LemmaType.USER);
            lemma.setUuid(UUID.randomUUID().toString());
            new LemmaDao().persist(lemma);
            return Response.ok(lemma).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response put(Lemma lemma) {
        if (lemma == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        LemmaDao lemmaDao = new LemmaDao();

        if (lemma != null && lemma.getId() instanceof Integer) {
            Lemma persistentLemma = lemmaDao.find(lemma.getId());

            if (persistentLemma != null) {
                if (wasCreatedByUsers(persistentLemma)) {
                    lemma.setSource(Source.LemmaType.USER);
                    lemma.setUuid(persistentLemma.getUuid());
                    Lemma mergedLemma = lemmaDao.merge(lemma);
                    return Response.ok(mergedLemma).type(MediaType.APPLICATION_JSON).build();
                } else {
                    return Response.status(Response.Status.FORBIDDEN).build();
                }
            }
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        LemmaDao lemmaDao = new LemmaDao();
        Lemma lemma = lemmaDao.find(id);

        if (lemma != null) {
            if (wasCreatedByUsers(lemma)) {
                lemmaDao.remove(lemma);
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * Checks if a lemma was created by users.
     *
     * @param lemma lemma to check
     * @return True, if a lemma’s source is correct, false otherwise.
     */
    private Boolean wasCreatedByUsers(Lemma lemma) {
        Source.LemmaType source = lemma.getSource();

        if (source != null) {
            if (source.equals(Source.LemmaType.USER)) {
                return true;
            }
        }

        return false;
    }
}
