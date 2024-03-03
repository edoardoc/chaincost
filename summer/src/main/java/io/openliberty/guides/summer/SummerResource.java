package io.openliberty.guides.summer;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import io.openliberty.guides.summer.client.ClearingCostClient;
import io.openliberty.guides.summer.client.ExternalBinClient;
import io.openliberty.guides.summer.client.IincacheClient;
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
  private static final AtomicInteger requestCount = new AtomicInteger(0);
  private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private static long nextAvailableTime = 0;

  @Inject
  SummerManager manager;

  @Inject
  ClearingCostClient clearingCostClient;
  
  @Inject
  IincacheClient iincacheClient;
  
  @Inject
  ExternalBinClient externalBinClient;

  @POST
  @Path("/payment-cards-cost")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getClearingCostForCard(@FormParam("card_number") String cardNumber) {
    // the first 8 digits of the card number is the bin / iin
    String bin = cardNumber.substring(0, 8);


    // consult iincache to get the country code for the card (if is in cache)
    Properties props = iincacheClient.getCountry(bin);
    if (props == null) {
      return Response.status(Response.Status.NOT_FOUND)
                     .entity("{ \"error\" : \"The target system service "
                     + "may not be running "  + "\" }")
                     .build();
    }



    // If not in cache, fetch data from external REST endpoint

    long currentTime = System.currentTimeMillis();
    if (requestCount.get() >= 5 && currentTime < nextAvailableTime) {
        // If rate limit is reached and it's not time for the next request yet,
        // delay the request until the next available time
        Thread.sleep(nextAvailableTime - currentTime);
    }

    Properties externalProps = externalBinClient.getCard(bin);

/*
 * 
 * 
 {
  "number": {},
  "scheme": "visa",
  "type": "debit",
  "brand": "Visa Classic",
  "country": {
    "numeric": "208",
    "alpha2": "DK",
    "name": "Denmark",
    "emoji": "🇩🇰",
    "currency": "DKK",
    "latitude": 56,
    "longitude": 10
  },
  "bank": {
    "name": "Jyske Bank A/S"
  }
}
 */


    // retrieving the country code from the response
    String country = externalProps.getProperty("country.alpha2");
    System.err.println("service says country is " + country);

    // Get cost for this country
    Properties props = clearingCostClient.getCost(country);
    if (props == null) {
      return Response.status(Response.Status.NOT_FOUND)
                     .entity("{ \"error\" : \"The target system service "
                     + "may not be running "  + "\" }")
                     .build();
    }

    return Response.ok(props).build();
  }
}