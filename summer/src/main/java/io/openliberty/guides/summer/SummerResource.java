package io.openliberty.guides.summer;

import java.util.Properties;

import io.openliberty.guides.summer.client.ClearingCostClient;
import io.openliberty.guides.summer.client.ExternalBinClient;
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
  
  @Inject
  ExternalBinClient externalBinClient;

  @POST
  @Path("/payment-cards-cost")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getClearingCostForCard(@FormParam("card_number") String cardNumber) {
    Properties props = clearingCostClient.getCost(country);

    // Get cost for this country
    Properties props = clearingCostClient.getCost(country);
    if (props == null) {
      return Response.status(Response.Status.NOT_FOUND)
                     .entity("{ \"error\" : \"The target system service "
                     + "may not be running "  + "\" }")
                     .build();
    }

    // return Response.ok(props).build();
  }

}
/*
 * 
    // need the first 6 digits of the card number to get the country code from external api
    String bin = cardNumber.substring(0, 6);
    Properties externalProps = externalBinClient.getCard(bin);

/*{
  "Status": "SUCCESS",
  "Scheme": "MASTERCARD",
  "Type": "CREDIT",
  "Issuer": "COMMONWEALTH BANK OF AUSTRALIA",
  "CardTier": "STANDARD",
  "Country": {
    "A2": "AU",
    "A3": "AUS",
    "N3": "036",
    "ISD": "61",
    "Name": "Australia",
    "Cont": "Oceania"
  }, 
  retrieving the country code from the response
  */
  String country = externalProps.getProperty("Country.A2");

  return Response.ok(externalProps).build();


 * 
 */