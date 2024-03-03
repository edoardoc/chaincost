package it.io.openliberty.guides.clearingcost;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.openliberty.guides.clearingcost.model.ClearingcostData;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

public class ClearingcostEndpointIT {

    private static String clusterUrl;

    private Client client;

    @BeforeAll
    public static void oneTimeSetup() {
        String nodePort = System.getProperty("clearingcost.http.port");
        clusterUrl = "http://localhost:" + nodePort + "/clearingcost/";
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
    public void testListContents() {
        WebTarget target = client.target(clusterUrl);
        Response response = target.request().get();
        
        assertEquals(200, response.getStatus(),
            "Incorrect response code from " + clusterUrl);
        response.close();
    }

    @Test
    public void testGetClearingcostForCountry() {
        WebTarget target = client.target(clusterUrl + "US");
        Response response = target.request().get();

        assertEquals(200, response.getStatus(),
            "Incorrect response code from " + clusterUrl + "US");
        response.close();
    }

    @Test
    public void testAddClearingcost() {
        WebTarget target = client.target(clusterUrl);
        Response response = target.request().put(Entity.json(new ClearingcostData("US", new BigDecimal("1.0"))));

        assertEquals(200, response.getStatus(),
            "Incorrect response code from " + clusterUrl);
        response.close();
    }

    @Test
    public void testInvalidCountryCode() {
        WebTarget target = client.target(clusterUrl + "USA");
        Response response = target.request().get();

        assertEquals(400, response.getStatus(),
            "Incorrect response code from " + clusterUrl + "USA");
        response.close();
    }

    @Test
    public void testInvalidCost() {
      WebTarget target = client.target(clusterUrl);
      Response response = target.request().put(Entity.json(new ClearingcostData("US", new BigDecimal("-5.0"))));

      assertEquals(400, response.getStatus(),
        "Incorrect response code from " + clusterUrl);
      response.close();
    }

}
