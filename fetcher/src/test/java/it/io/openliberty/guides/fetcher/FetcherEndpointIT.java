package it.io.openliberty.guides.fetcher;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

public class FetcherEndpointIT {

    private static String clusterUrl;

    private Client client;

    @BeforeAll
    public static void oneTimeSetup() {
        String nodePort = System.getProperty("fetcher.http.port");
        clusterUrl = "http://localhost:" + nodePort + "/fetcher/";
    }

    @BeforeEach
    public void setup() {
        client = ClientBuilder.newBuilder()
                    .hostnameVerifier(new HostnameVerifier() {
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .build();
    }

    @AfterEach
    public void teardown() {
        client.close();
    }


    @Test
    public void testAddToFetcher() {
        WebTarget target = client.target(clusterUrl);
        Response response = target.request().put(Entity.json("123456"));
        assertEquals(200, response.getStatus(), "Incorrect response code from " + clusterUrl);
        response.close();
    }

    @Test
    public void testAddToFetcherWithInvalidIIN() {
        WebTarget target = client.target(clusterUrl);
        Response response = target.request().put(Entity.json("12345"));
        assertEquals(400, response.getStatus(), "Incorrect response code from " + clusterUrl);
        response.close();
    }

    @Test
    public void testAddToFetcherWithInvalidIIN2() {
        WebTarget target = client.target(clusterUrl);
        Response response = target.request().put(Entity.json("123456789"));
        assertEquals(400, response.getStatus(), "Incorrect response code from " + clusterUrl);
        response.close();
    }

    @Test
    public void testAddToFetcherWithInvalidIIN3() {
        WebTarget target = client.target(clusterUrl);
        Response response = target.request().put(Entity.json("1234567"));
        assertEquals(400, response.getStatus(), "Incorrect response code from " + clusterUrl);
        response.close();
    }

}
