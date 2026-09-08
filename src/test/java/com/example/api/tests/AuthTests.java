package com.example.api.tests;

import com.example.api.base.BaseTest;
import com.example.api.config.ConfigManager;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthTests extends BaseTest {

    @Test(groups={"regression","smoke"})
    public void validCredentialsReturnToken() {
        authClient.createTokenResponse(ConfigManager.username(), ConfigManager.password())
                .then().statusCode(200)
                .body("token", notNullValue());
    }

    @Test(groups={"regression","negative"})
    public void invalidCredentialsReturnReason() {
        authClient.createTokenResponse("invalid", "invalid")
                .then().statusCode(200)
                .body("reason", notNullValue());
    }

    @Test(groups={"regression","negative"})
    public void missingUsernameIsRejected() {
        given(requestSpec)
                .body("{\"password\":\"password123\"}")
                .when().post("/auth")
                .then().statusCode(200)
                .body("reason", notNullValue());
    }

    @Test(groups={"regression","negative"})
    public void missingPasswordIsRejected() {
        given(requestSpec)
                .body("{\"username\":\"admin\"}")
                .when().post("/auth")
                .then().statusCode(200)
                .body("reason", notNullValue());
    }

    @Test(groups={"regression","negative"})
    public void emptyCredentialsAreRejected() {
        given(requestSpec)
                .body("{\"username\":\"\",\"password\":\"\"}")
                .when().post("/auth")
                .then().statusCode(200)
                .body("reason", notNullValue());
    }
}
