package com.example.api.base;

import org.testng.annotations.BeforeClass;

import com.example.api.clients.AuthClient;
import com.example.api.clients.BookingClient;
import com.example.api.config.ConfigManager;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public abstract class BaseTest {
    protected RequestSpecification requestSpec;
    protected AuthClient authClient;
    protected BookingClient bookingClient;
    protected String token;

    @BeforeClass(alwaysRun = true)
    public void setUpFramework() {
       requestSpec = new RequestSpecBuilder()
                .setBaseUri(ConfigManager.baseUri())
                .setContentType(ContentType.JSON)
                .setAccept("application/json")
                .build();

        authClient = new AuthClient(requestSpec);
        bookingClient = new BookingClient(requestSpec);
    }

    protected void authenticate() {
        Response response = authClient.createTokenResponse(
                ConfigManager.username(),
                ConfigManager.password());

        token = response.then()
                .statusCode(200)
                .extract()
                .path("token");
    }
}
