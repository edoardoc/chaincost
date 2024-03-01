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
public class ExternalBinClient {

    @Inject
    @ConfigProperty(name = "externalbin.host", defaultValue = "data.handyapi.com")
    String externalbinHost;
    @Inject
    @ConfigProperty(name = "externalbin.protocol", defaultValue = "https")
    String externalbinProtocol;
    @Inject
    @ConfigProperty(name = "externalbin.port", defaultValue = "443")
    String externalbinPort;
    @Inject
    @ConfigProperty(name = "externalbin.query", defaultValue = "/bin")
    String externalbinQuery;

    // Wrapper function that gets the response and returns as properties
    public Properties getCard(String bin) {
        Properties properties = null;
        Client client = ClientBuilder.newClient();
        try {
            Builder builder = getBuilder(bin, client);
            properties = getCardHelper(builder);
        } catch (Exception e) {
            System.err.println(
            "Exception thrown while getting properties: " + e.getMessage());
        } finally {
            client.close();
        }
        return properties;
    }

    private Builder getBuilder(String bin, Client client) throws Exception {
        URI uri = new URI(
                      externalbinProtocol, null, externalbinHost, 443,
                      externalbinQuery + "/" + bin, null, null);
        String urlString = uri.toString();
        Builder builder = client.target(urlString).request();
        builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        return builder;
    }

    // Helper method that processes the request
    private Properties getCardHelper(Builder builder) throws Exception {
        Response response = builder.get();
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(Properties.class);
        } else {
            System.err.println("Response Status is not OK.");
            return null;
        }
    }
}
