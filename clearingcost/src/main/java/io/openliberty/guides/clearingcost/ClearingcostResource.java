package io.openliberty.guides.clearingcost;

import io.openliberty.guides.clearingcost.model.ClearingcostData;
import io.openliberty.guides.clearingcost.model.ClearingcostList;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/costs")
public class ClearingcostResource {

  @Inject
  ClearingcostManager manager;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public ClearingcostList listContents() {
    return manager.list();
  }


  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response addClearingcost(@Valid ClearingcostData clearingcost) {
    if (clearingcost.getCountry().length() != 2) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Country code must be 2 characters").build();
    }
    if (clearingcost.getCost().compareTo(new java.math.BigDecimal(0)) < 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Invald Cost").build();
    }
    try {
      manager.add(clearingcost);
    } catch (Exception e) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Error, Clearing Cost was not added " + e.getMessage()).build();
    } 
    return Response.ok(clearingcost).build();
  }

  
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


  @POST
  @Path("/reset")
  public void reset() {
    manager.reset();
  }
}
