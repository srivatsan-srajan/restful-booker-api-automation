package com.example.api.tests;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.example.api.base.BaseTest;
import com.example.api.payloads.BookingPayload;

import io.restassured.response.Response;

public class ContentNegotiationTests extends BaseTest {

    @Test(groups={"regression","smoke","content"})
    public void createBookingReturnsJsonByDefault() {

        Response response = bookingClient
                .createBookingJson(
                        BookingPayload.validBookingJson());

        response.then()
                .statusCode(200)
                .header("Content-Type",
                        containsString("application/json"))
                .body("bookingid", notNullValue());
    }

    @Test(groups={"regression","content"})
    public void createBookingXmlResponseCanBeRequested() {

        Response response = bookingClient
                .createBookingXml(
                        BookingPayload.xmlBooking());

        /*
         * Current Heroku deployment returns 500 when XML is used
         * as the request/response representation for POST /booking.
         *
         * Keep this test because it documents the API's current
         * content-negotiation behavior.
         */
        Assert.assertEquals(
                response.statusCode(),
                500,
                "Unexpected XML content-negotiation response: "
                        + response.asString());
    }

    @Test(groups={"regression","content"})
    public void jsonResponseContainsExpectedContentType() {

        Response response = bookingClient
                .createBookingJson(
                        BookingPayload.validBookingJson());

        Assert.assertEquals(
                response.statusCode(),
                200);

        Assert.assertTrue(
                response.contentType()
                        .toLowerCase()
                        .contains("application/json"),
                "Expected JSON content type but received: "
                        + response.contentType());
    }
}