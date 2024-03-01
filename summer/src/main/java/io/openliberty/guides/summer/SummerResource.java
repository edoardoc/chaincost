package io.openliberty.guides.summer;

import java.util.Properties;

import io.openliberty.guides.summer.client.ClearingCostClient;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/api/v1")
public class SummerResource {

  @Inject
  SummerManager manager;

  @Inject
  ClearingCostClient clearingCostClient;

  @POST
  @Path("/payment-cards-cost")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getClearingCostForCard(@FormParam("card_number") String cardNumber) {
    // need the first 6 digits of the card number to get the country code from external api
    String bin = cardNumber.substring(0, 6);

    // for now assuming the country code is US
    String country = "US";

    // Get cost for this country
    Properties props = clearingCostClient.getCost(country);
    if (props == null) {
      return Response.status(Response.Status.NOT_FOUND)
                     .entity("{ \"error\" : \"Unknown hostname or the system service "
                     + "may not be running on BLAH BLAH "  + "\" }")
                     .build();
    }

    // Add to summer
    return Response.ok(props).build();
  }

}
