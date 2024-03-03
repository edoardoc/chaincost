package io.openliberty.guides.summer.client;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.Properties;
import java.net.URI;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@RequestScoped
public class ClearingCostClient {

    // Constants for building URI to the system service.
    private final String CLEARING_COST = "/clearingcost";
    private final String PROTOCOL = "http";

    @Inject
    @ConfigProperty(name = "clearingcost.http.port", defaultValue = "9080")
    String clearingcostHttpPort;
    @Inject
    @ConfigProperty(name = "clearingcost.ip", defaultValue = "localhost")
    String clearingcostIP;

    // Wrapper function that gets properties
    public Properties getCost(String countrycode) {
        Properties properties = null;
        Client client = ClientBuilder.newClient();
        try {
            Builder builder = getBuilder(countrycode, client);
            properties = getClearingcostHelper(builder);
        } catch (Exception e) {
            System.err.println(
            "Exception thrown while getting properties: " + e.getMessage());
        } finally {
            client.close();
        }
        return properties;
    }

    // Method that creates the client builder
    private Builder getBuilder(String countrycode, Client client) throws Exception {
        URI uri = new URI(
                      PROTOCOL, null, clearingcostIP, Integer.valueOf(clearingcostHttpPort),
                      CLEARING_COST + "/" + countrycode, null, null);
        String urlString = uri.toString();
        Builder builder = client.target(urlString).request();
        builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        return builder;
    }

    // Helper method that processes the request
    private Properties getClearingcostHelper(Builder builder) throws Exception {
        Response response = builder.get();
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(Properties.class);
        } else {
            System.err.println("getClearingcostHelper Response Status is not OK.");
            return null;
        }
    }
}
