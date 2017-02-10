package lemming.context;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("contexts")
public class ContextResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        List<Context> list = new ContextDao().getAll();
        return Response.ok(list).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") String id) {
        Context context = new ContextDao().find(Integer.valueOf(id));

        if (context != null) {
            return Response.ok(context).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/keyword/{keyword}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByKeyword(@PathParam("keyword") String keyword) {
        List<Context> list = new ContextDao().findByKeywordStart(keyword);
        return Response.ok(list).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(Context context) {
        if (context != null) {
            context.setUuid(UUID.randomUUID().toString());
            new ContextDao().persist(context);
            return Response.ok(context).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response put(Context context) {
        if (context == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        ContextDao contextDao = new ContextDao();

        if (context != null && context.getId() instanceof Integer) {
            Context persistentContext = contextDao.find(context.getId());

            if (persistentContext != null) {
                context.setUuid(persistentContext.getUuid());
                Context mergedContext = contextDao.merge(context);
                return Response.ok(mergedContext).type(MediaType.APPLICATION_JSON).build();
            }
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        ContextDao contextDao = new ContextDao();
        Context context = contextDao.find(id);

        if (context != null) {
            contextDao.remove(context);
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
