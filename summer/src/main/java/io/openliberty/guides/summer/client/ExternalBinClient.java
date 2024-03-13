package io.openliberty.guides.summer.client;

import java.io.StringReader;
import java.net.URI;
import java.util.logging.Logger;

import io.openliberty.guides.summer.client.utils.CardNotFoundException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@RequestScoped
public class ExternalBinClient {
  private static final Logger LOGGER = Logger.getLogger(ExternalBinClient.class.getName());

  private final String EXTERNAL_BIN_HOST = "lookup.binlist.net";
  private final String EXTERNAL_BIN_PROTOCOL = "https";
  private final int EXTERNAL_BIN_PORT = 443;
  private final String EXTERNAL_BIN_QUERY = "/";

    // Wrapper function that gets the response and returns as properties
    public String getCard(String bin) throws Exception {
        String outTwo = null;
        try (Client client = ClientBuilder.newClient()) {
            Builder builder = getBuilder(bin, client);
            outTwo = getCardHelper(builder);
        } catch (Exception e) {
          throw e;
        }
        return outTwo;
    }

    private Builder getBuilder(String bin, Client client) throws Exception {
        URI uri = new URI(
                      EXTERNAL_BIN_PROTOCOL, null, EXTERNAL_BIN_HOST, EXTERNAL_BIN_PORT,
                      EXTERNAL_BIN_QUERY + bin, null, null);
        String urlString = uri.toString();
        Builder builder = client.target(urlString).request();
        builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        builder.header("Accept-Version", "3"); // for the binlist API only
        return builder;
    }

    // Helper method that processes the request
    private String getCardHelper(Builder builder) throws Exception {
      String countryTwoLetter = null;
        Response response = builder.get();
        if (response.getStatus() == Status.OK.getStatusCode()) {
          String JSONResponse = response.readEntity(String.class);
          JsonReader jsonReader = Json.createReader(new StringReader(JSONResponse));
          JsonObject jsonObject = jsonReader.readObject();
          if (response.getStatus() != Status.OK.getStatusCode()) {
            throw new CardNotFoundException("Card not found");
          }
          try {
            countryTwoLetter = jsonObject.getJsonObject("Country").getString("A2");
          } catch (Exception e) {
              throw new CardNotFoundException("Card not found");
          }
          return countryTwoLetter;
        } else {
          LOGGER.severe("getCardHelper Response Status is not OK.");
          return null;
        }
    }
}
