package io.openliberty.guides.summer.client;

import java.net.URI;
import java.util.Properties;
import java.util.logging.Logger;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.openliberty.guides.summer.SummerResource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@RequestScoped
public class InncacheClient {
    private static final Logger logger = Logger.getLogger(SummerResource.class.getName());
    private final String INN_CACHE = "/inncache";
    private final String PROTOCOL = "http";

    @Inject
    @ConfigProperty(name = "inncache.http.port", defaultValue = "9080")
    String inncacheHttpPort;
    @Inject
    @ConfigProperty(name = "inncache.ip", defaultValue = "localhost")
    String inncacheIP;

    // Wrapper function that gets properties
    public Properties getCountry(String inncode) throws Exception {
        Properties properties = null;
        Client client = ClientBuilder.newClient();
        try {
            Builder builder = getBuilder(inncode, client);
            properties = getInncacheHelper(builder);
        } catch (Exception e) {
          logger.severe("Exception thrown while getting properties: " + e.getMessage());
        } finally {
            client.close();
        }
        return properties;
    }

    // Method that creates the client builder
    private Builder getBuilder(String inncode, Client client) throws Exception {
        URI uri = new URI(
                      PROTOCOL, null, inncacheIP, Integer.valueOf(inncacheHttpPort),
                      INN_CACHE + "/" + inncode, null, null);
        String urlString = uri.toString();
        Builder builder = client.target(urlString).request();
        builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        return builder;
    }

    // Helper method that processes the request
    private Properties getInncacheHelper(Builder builder) throws Exception {
        Response response = builder.get();
        
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(Properties.class);
        } else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
            // if the response is not found, means that the item is not in the cache,
            // so we return null and throw an exception CachenotfoundException
            Throwable t = new Throwable("Cache not found");
            return null;
        } else {
          logger.severe("getInncacheHelper Response Status is not OK.");
          return null;
        }
    }
}
