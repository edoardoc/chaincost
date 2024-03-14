package io.openliberty.guides.summer.client;

import java.net.URI;
import java.util.Properties;
import java.util.logging.Logger;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.openliberty.guides.summer.client.utils.CacheNotFoundException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@RequestScoped
public class IincacheClient {
    private static final Logger LOGGER = Logger.getLogger(IincacheClient.class.getName());
    private final String IIN_CACHE = "/iincache";
    private final String PROTOCOL = "http";

    @Inject
    @ConfigProperty(name = "iincache.http.port", defaultValue = "9082")
    String iincacheHttpPort;
    @Inject
    @ConfigProperty(name = "iincache.ip", defaultValue = "localhost")
    String iincacheIP;

    public Properties getCountry(String iincode) throws Exception {
      Properties properties = null;
      try (Client client = ClientBuilder.newClient()) {
          Builder builder = getBuilder(iincode, client);
          properties = getIincacheHelper(builder);
      }
      return properties;
    }

    // Method that creates the client builder
    private Builder getBuilder(String iincode, Client client) throws Exception {
      URI uri = new URI(
                    PROTOCOL, null, iincacheIP, Integer.valueOf(iincacheHttpPort),
                    IIN_CACHE + "/" + iincode, null, null);
      String urlString = uri.toString();
      Builder builder = client.target(urlString).request();
      builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
      return builder;
    }

    public void saveCountry(String iincode, String country) throws Exception {
      try (Client client = ClientBuilder.newClient()) {
        Builder builder = getBuilder(iincode, client);
        Response saveC = builder.put(Entity.entity(country, MediaType.APPLICATION_JSON));
        if (saveC.getStatus() != Status.OK.getStatusCode()) {
          LOGGER.severe("saveCountry Response Status was not OK, could not save the country " + country + " for iincode " + iincode + " in the cache" );
        }
      }
    }

    private Properties getIincacheHelper(Builder builder) throws CacheNotFoundException {
      Response response = builder.get();
      if (response.getStatus() == Status.OK.getStatusCode()) {
          return response.readEntity(Properties.class);
      } else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
          // if the response is not found, it means that the item is not in the cache,
          // so we return null and throw an exception CachenotfoundException
          throw new CacheNotFoundException("Item not found in cache");
      } else {
        LOGGER.severe("getIincacheHelper Response Status is not OK.");
        return null;
      }
    }
}
