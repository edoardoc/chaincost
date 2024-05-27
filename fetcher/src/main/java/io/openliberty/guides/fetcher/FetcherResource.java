package io.openliberty.guides.fetcher;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/*
 * 
 * whenever its main endpoint gets invoked (with a bin that is not in the cache), adds that bin in its queue, IF NOT ALREADY PRESENT!
There is an internal synchronised (Through a ScopeApplication inject?) consumer task that:
1 - fetches an element from the queue
2 - invokes the external endpoint
3 - if it returns 429, sleeps for an hour (it is guaranteed that nobody else will hit the endpoint during this time)
4 - if it returns 200, updates the cache and removes the element from the queue
5 - goto 1
 * 
 */
@RequestScoped
@Path("/")
public class FetcherResource {

  @Inject
  FetcherManager manager;

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response addFetcher(String bin) {
    if ((bin.length() != 6) && (bin.length() != 8)) {
      return Response.status(Response.Status.BAD_REQUEST).entity("IIN code must be 6 or 8 characters").build();
    }
    try {
        manager.store(bin);
    } catch (Exception e) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Error, IIN was not added: " + e.getMessage()).build();
    } 
    return Response.ok(bin).build();
  }

  
}
