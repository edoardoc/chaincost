package io.openliberty.guides.iincache;

import io.openliberty.guides.iincache.model.IINCacheData;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
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
  public Response getIINCache(@PathParam("iin") String iin) {
    try {
        if (iin.length() != 8) {
          return Response.status(Response.Status.BAD_REQUEST).entity("IIN code must be 8 characters").build();
        }
        IINCacheData IINCache = manager.get(iin);
        if (IINCache == null) {
            // the cache has no matching IIN code
            return Response.status(Response.Status.NOT_FOUND).entity("IIN code not in cache").build();
        }
        return Response.ok(IINCache).build();
    } catch (Exception e) {
        return Response.status(Response.Status.BAD_REQUEST).entity("Invalid IIN code: " + e.getMessage()).build();
    }
  }

  
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{iin}")
  public Response setIINCache(@PathParam("iin") String iin, String alpha2) {
    if (iin.length() != 8) {
      return Response.status(Response.Status.BAD_REQUEST).entity("IIN code must be 8 characters").build();
    }
    if (alpha2.length() != 2) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Country code must be 2 characters").build();
    }
    alpha2 = alpha2.toUpperCase();
    IINCacheData iinData = new IINCacheData(iin, alpha2);
    try {
      alpha2 = alpha2.toUpperCase();
        manager.store(iinData);
    } catch (Exception e) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Error, IINCache Cost was not added: " + e.getMessage()).build();
    } 
    return Response.ok(iinData).build();
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
