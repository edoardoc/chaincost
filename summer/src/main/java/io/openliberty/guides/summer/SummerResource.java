package io.openliberty.guides.summer;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import io.openliberty.guides.summer.client.ClearingCostClient;
import io.openliberty.guides.summer.client.ExternalBinClient;
import io.openliberty.guides.summer.client.IincacheClient;
import io.openliberty.guides.summer.client.utils.CacheNotFoundException;
import io.openliberty.guides.summer.client.utils.CardNotFoundException;
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
  // need referendce to logger
  private static final Logger logger = Logger.getLogger(SummerResource.class.getName());

  private static final AtomicInteger requestCount = new AtomicInteger(0);
  private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private static long nextAvailableTime = 0;

  @Inject
  ClearingCostClient clearingCostClient;
  
  @Inject
  IincacheClient iincacheClient;
  
  @Inject
  ExternalBinClient externalBinClient;

  @POST
  @Path("/payment-cards-cost")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getClearingCostForCard(@FormParam("card_number") String cardNumber) throws InterruptedException {
    String country = null;
    // the first 8 digits of the card number is the bin / iin
    String bin = cardNumber.substring(0, 8);

    // consult iincache to get the country code for the card (if is in cache)
    try {
      Properties props = iincacheClient.getCountry(bin);
      logger.info("MATCH! props is " + props);
      if (props != null) {
        country = props.getProperty("alpha2");

        Properties props2 = clearingCostClient.getCost(country);
        if (props2 == null) {
          return Response.status(Response.Status.NOT_FOUND)
                         .entity("{ \"error\" : \"The target system service ClearingCost "
                         + "may not be running "  + "\" }")
                         .build();
        }
        return Response.ok(props2).build(); 
      }
    } catch (CacheNotFoundException e) {
      // no cache found, continue with the next step
      // fetch data from external REST endpoint
      long currentTime = System.currentTimeMillis();
      if (requestCount.get() >= 5 && currentTime < nextAvailableTime) {
          // If rate limit is reached and it's not time for the next request yet,
          // delay the request until the next available time
          Thread.sleep(nextAvailableTime - currentTime);
      }

      try {
        country = externalBinClient.getCard(bin);
      } catch (CardNotFoundException ef) {
        return Response.status(Response.Status.NOT_FOUND)
                       .entity("{ \"error\" : \"The card could not be found by "
                       + "ExternalBinClient - "  + ef.getMessage() + "\" }")
                       .build();
      } catch (Exception ef) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                       .entity("{ \"error\" : \"The target system service ExternalBinClient "
                       + "may not be running "  + "\" }")
                       .build();
      }
      // lets save the country in the cache
      try {
        iincacheClient.saveCountry(bin, country);
      } catch (Exception e1) {
        logger.warning("Failed to save country in cache: " + e1.getMessage());
      }

      // update the rate limit
      // Increment request count and schedule next available time
      requestCount.incrementAndGet();
      if (requestCount.get() >= 5) {
          nextAvailableTime = currentTime + TimeUnit.HOURS.toMillis(1);
          scheduler.schedule(() -> requestCount.set(0), 1, TimeUnit.HOURS);
      }      
      
    } catch (Exception e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity("{ \"error\" : \"The target system service INNCache "
                         + "may not be running "  + "\" }")
                         .build();
    }

    // Get cost for this country
    Properties props = clearingCostClient.getCost(country);
    if (props == null) {
      return Response.status(Response.Status.NOT_FOUND)
                     .entity("{ \"error\" : \"The target system service ClearingCost "
                     + "may not be running "  + "\" }")
                     .build();
    }
    return Response.ok(props).build();
  }
}