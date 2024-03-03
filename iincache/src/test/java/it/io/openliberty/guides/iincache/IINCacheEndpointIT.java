package it.io.openliberty.guides.iincache;

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

public class IINCacheEndpointIT {

    private static String clusterUrl;

    private Client client;

    @BeforeAll
    public static void oneTimeSetup() {
      String nodePort = System.getProperty("IINCache.http.port");
      clusterUrl = "http://localhost:" + nodePort + "/iincache/";
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
    public void testPutIINCache() {
      WebTarget target = client.target(clusterUrl + "51786254");
      Response response = target.request().put(Entity.json(new String("KZ")));
      assertEquals(200, response.getStatus(), "Incorrect response code from " + clusterUrl);
      response.close();
    }

    @Test
    public void testIINCache() {
      WebTarget target = client.target(clusterUrl + "12345678");
      Response response = target.request().get();
      assertEquals(404, response.getStatus(), "Incorrect response code from " + clusterUrl);
      response.close();
    }

    @Test
    public void testDeleteIINCache() {
      WebTarget target = client.target(clusterUrl + "51786254");
      Response response = target.request().delete();
      assertEquals(200, response.getStatus(), "Incorrect response code from " + clusterUrl);
      response.close();
    }

    @Test
    public void testInvalidIINCache() {
      WebTarget target = client.target(clusterUrl + "51786254");
      Response response = target.request().get();
      assertEquals(404, response.getStatus(), "Incorrect response code from " + clusterUrl);
      response.close();
    }
}
