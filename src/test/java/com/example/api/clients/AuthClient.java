package com.example.api.clients;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class AuthClient {
    private final RequestSpecification spec;

    public AuthClient(RequestSpecification spec) {
        this.spec = spec;
    }

    public Response createTokenResponse(String username, String password) {
        return given(spec)
                .body("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}")
                .when()
                .post("/auth");
    }

    public String createToken(String username, String password) {
        return createTokenResponse(username, password)
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }
}
