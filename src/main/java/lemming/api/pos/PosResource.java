package lemming.api.pos;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("pos")
public class PosResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        List<Pos> list = new PosDao().getAll();
        return Response.ok(list).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") String id) {
        Pos pos = new PosDao().find(Integer.valueOf(id));

        if (pos != null) {
            return Response.ok(pos).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/name/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByName(@PathParam("name") String name) {
        List<Pos> list = new PosDao().findByName(name);
        return Response.ok(list).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(Pos pos) {
        if (pos != null) {
            pos.setOrigin(Origin.Type.USER);
            pos.setUuid(UUID.randomUUID().toString());
            new PosDao().persist(pos);
            return Response.ok(pos).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response put(Pos pos) {
        if (pos == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        PosDao posDao = new PosDao();

        if (pos != null && pos.getId() instanceof Integer) {
            Pos persistentPos = posDao.find(pos.getId());

            if (persistentPos != null) {
                if (wasCreatedByUsers(persistentPos)) {
                    pos.setOrigin(Origin.Type.USER);
                    pos.setUuid(persistentPos.getUuid());
                    Pos mergedPos = posDao.merge(pos);
                    return Response.ok(mergedPos).type(MediaType.APPLICATION_JSON).build();
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
        PosDao posDao = new PosDao();
        Pos pos = posDao.find(id);

        if (pos != null) {
            if (wasCreatedByUsers(pos)) {
                posDao.remove(pos);
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * Checks if a part of speech was created by users.
     *
     * @param pos part of speech to check
     * @return True, if a part of speech’s origin is correct, false otherwise.
     */
    private Boolean wasCreatedByUsers(Pos pos) {
        Origin.Type origin = pos.getOrigin();

        if (origin != null) {
            if (origin.equals(Origin.Type.USER)) {
                return true;
            }
        }

        return false;
    }
}
