package io.openliberty.guides.iincache;

import io.openliberty.guides.iincache.model.IINCacheData;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/")
public class IINCacheResource {

  @Inject
  IINCacheManager manager;

  @GET
  @Path("/{iin}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getIINCacheForiin(@PathParam("iin") String iin) {
    try {
        if ((iin.length() != 6) && (iin.length() != 8)) {
            throw new Exception("IIN code must be 6 or 8 characters");
        }
        IINCacheData IINCache = manager.get(iin);
        if (IINCache == null) {
            // the cache has no match, must invoke the external service

        }
        
        return Response.ok(IINCache).build();
    } catch (Exception e) {
        return Response.status(Response.Status.BAD_REQUEST).entity("Invalid IIN code: " + e.getMessage()).build();
    }
  }
  
  @DELETE
  @Path("/{iin}")
  public Response deleteIINCacheForiin(@PathParam("iin") String iin) {
    if (manager.get(iin) == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("IIN code not found").build();
    }
    manager.remove(iin);
    return Response.ok().build();
  }
}
