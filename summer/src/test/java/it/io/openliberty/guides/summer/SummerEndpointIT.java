// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2018, 2023 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
// end::copyright[]
package it.io.openliberty.guides.summer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.json.JsonObject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
public class SummerEndpointIT {

    private static String invUrl;
    private static String sysUrl;
    private static String systemServiceIp;

    private static Client client;

    @BeforeAll
    public static void oneTimeSetup() {

        String invServPort = System.getProperty("summer.http.port");
        String sysServPort = System.getProperty("clearingcost.http.port");

        // tag::systemServiceIp[]
        systemServiceIp = System.getProperty("system.ip");
        // end::systemServiceIp[]

        invUrl = "http://localhost" + ":" + invServPort + "/summer/systems/";
        sysUrl = "http://localhost" + ":" + sysServPort + "/clearingcost/properties/";

        client = ClientBuilder.newBuilder().hostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        }).build();

        client.target(invUrl + "reset").request().post(null);
    }

    @AfterAll
    public static void teardown() {
        client.close();
    }

    // tag::tests[]
    // tag::testEmptySummer[]
    @Test
    @Order(1)
    public void testEmptySummer() {
        Response response = this.getResponse(invUrl);
        this.assertResponse(invUrl, response);

        JsonObject obj = response.readEntity(JsonObject.class);

        int expected = 0;
        int actual = obj.getInt("total");
        assertEquals(expected, actual,
                    "The summer should be empty on application start but it wasn't");

        response.close();
    }
    // end::testEmptySummer[]

    // tag::testHostRegistration[]
    @Test
    @Order(2)
    public void testHostRegistration() {
        this.visitSystemService();

        Response response = this.getResponse(invUrl);
        this.assertResponse(invUrl, response);

        JsonObject obj = response.readEntity(JsonObject.class);

        int expected = 1;
        int actual = obj.getInt("total");
        assertEquals(expected, actual,
                        "The summer should have one entry for " + systemServiceIp);

        boolean serviceExists = obj.getJsonArray("systems").getJsonObject(0)
                        .get("hostname").toString().contains(systemServiceIp);
        assertTrue(serviceExists,
                        "A host was registered, but it was not " + systemServiceIp);

        response.close();
    }
    // end::testHostRegistration[]

    // tag::testSystemPropertiesMatch[]
    @Test
    @Order(3)
    public void testSystemPropertiesMatch() {
        Response invResponse = this.getResponse(invUrl);
        Response sysResponse = this.getResponse(sysUrl);

        this.assertResponse(invUrl, invResponse);
        this.assertResponse(sysUrl, sysResponse);

        JsonObject jsonFromSummer = (JsonObject) invResponse
                        .readEntity(JsonObject.class).getJsonArray("systems")
                        .getJsonObject(0).get("properties");

        JsonObject jsonFromSystem = sysResponse.readEntity(JsonObject.class);

        String osNameFromSummer = jsonFromSummer.getString("os.name");
        String osNameFromSystem = jsonFromSystem.getString("os.name");
        this.assertProperty("os.name", systemServiceIp, osNameFromSystem,
                        osNameFromSummer);

        String userNameFromSummer = jsonFromSummer.getString("user.name");
        String userNameFromSystem = jsonFromSystem.getString("user.name");
        this.assertProperty("user.name", systemServiceIp, userNameFromSystem,
                        userNameFromSummer);

        invResponse.close();
        sysResponse.close();
    }
    // end::testSystemPropertiesMatch[]

    // tag::testUnknownHost[]
    @Test
    @Order(4)
    public void testUnknownHost() {
        Response response = this.getResponse(invUrl);
        this.assertResponse(invUrl, response);

        Response badResponse = client.target(invUrl + "badhostname")
                        .request(MediaType.APPLICATION_JSON).get();

        String obj = badResponse.readEntity(String.class);

        boolean isError = obj.contains("error");
        assertTrue(isError,
                        "badhostname is not a valid host but it didn't raise an error");

        response.close();
        badResponse.close();
    }
    // end::testUnknownHost[]
    // end::tests[]

    // Returns response information from the specified URL.
    private Response getResponse(String url) {
        return client.target(url).request().get();
    }


    // Asserts that the given URL has the correct response code of 200.
    private void assertResponse(String url, Response response) {
        assertEquals(200, response.getStatus(), "Incorrect response code from " + url);
    }

    // Asserts that the specified JVM system property is equivalent in both the
    // system and summer services.
    private void assertProperty(String propertyName, String hostname, String expected,
                    String actual) {
        assertEquals(expected, actual, "JVM system property [" + propertyName + "] "
                        + "in the system service does not match the one stored in "
                        + "the summer service for " + hostname);
    }

    // Makes a simple GET request to summer/localhost.
    private void visitSystemService() {
        Response response = this.getResponse(sysUrl);
        this.assertResponse(sysUrl, response);
        response.close();

        Response targetResponse = client.target(invUrl + systemServiceIp).request()
                        .get();

        targetResponse.close();
    }
}
