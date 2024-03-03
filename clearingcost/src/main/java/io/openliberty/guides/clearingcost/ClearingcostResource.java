package io.openliberty.guides.clearingcost;

import java.math.BigDecimal;

import io.openliberty.guides.clearingcost.model.ClearingcostData;
import io.openliberty.guides.clearingcost.model.ClearingcostList;
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
public class ClearingcostResource {

  @Inject
  ClearingcostManager manager;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public ClearingcostList listContents() {
    return manager.list();
  }

  @GET
  @Path("/{country}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getClearingcostForCountry(@PathParam("country") String country) {
    try {
        if (country.length() != 2) {
            throw new Exception("Country code must be 2 characters");
        }
        country = country.toUpperCase();
        ClearingcostData clearingcost = manager.get(country);
        if (clearingcost == null) {
            clearingcost = new ClearingcostData(country, new BigDecimal("10.0"));   // the matrix has no match, return $10, other
        }
        
        return Response.ok(clearingcost).build();
    } catch (Exception e) {
        return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Country code: " + e.getMessage()).build();
    }
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response addClearingcost(ClearingcostData clearingcost) {
    if (clearingcost.getCountry().length() != 2) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Country code must be 2 characters").build();
    }
    if (clearingcost.getCost().compareTo(new java.math.BigDecimal(0)) < 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Invald Cost").build();
    }
    try {
        clearingcost.setCountry(clearingcost.getCountry().toUpperCase());
        manager.store(clearingcost);
    } catch (Exception e) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Error, Clearing Cost was not added: " + e.getMessage()).build();
    } 
    return Response.ok(clearingcost).build();
  }

  
  @DELETE
  @Path("/{country}")
  public Response deleteClearingcostForCountry(@PathParam("country") String country) {
    country = country.toUpperCase();
    if (manager.get(country) == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Country code not found").build();
    }
    manager.remove(country.toUpperCase());
    return Response.ok().build();
  }
}
