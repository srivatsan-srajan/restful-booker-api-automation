package com.example.api.tests;

import com.example.api.base.BaseTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class HealthTests extends BaseTest {

    @Test(groups={"regression","smoke"})
    public void verifyApiHealth() {
        given(requestSpec)
                .when().get("/ping")
                .then().statusCode(201);
    }

    @Test(groups={"regression"})
    public void verifyHealthResponseTime() {
        given(requestSpec)
                .when().get("/ping")
                .then().statusCode(201)
                .time(org.hamcrest.Matchers.lessThan(5000L));
    }
}
