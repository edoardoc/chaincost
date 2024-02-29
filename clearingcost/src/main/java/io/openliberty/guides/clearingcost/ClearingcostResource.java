package io.openliberty.guides.clearingcost;

import io.openliberty.guides.clearingcost.model.ClearingcostList;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@RequestScoped
@Path("/systems")
public class ClearingcostResource {

  @Inject
  ClearingcostManager manager;

//   @GET
//   @Path("/{country}")
//   @Produces(MediaType.APPLICATION_JSON)
//   public Response getPropertiesForHost(@PathParam("country") String country) {
//     if (country == null) {
//       return Response.status(Response.Status.NOT_FOUND)
//                      .entity("{ \"error\" : \"Unknown country or the Clearingcost service "
//                      + "may not be running on " + country + "\" }")
//                      .build();
//     }

//     // Add to summer
//     manager.add(country, props);
//     return Response.ok(props).build();
//   }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public ClearingcostList listContents() {
    return manager.list();
  }

  @POST
  @Path("/reset")
  public void reset() {
    manager.reset();
  }
}
