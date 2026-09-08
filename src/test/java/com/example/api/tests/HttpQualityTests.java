package com.example.api.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.example.api.base.BaseTest;
import com.example.api.models.Booking;
import com.example.api.models.BookingDates;

import io.restassured.response.Response;

public class HttpQualityTests extends BaseTest {

    private int bookingId;

    @BeforeClass(alwaysRun = true)
    public void createFixture() {

        authenticate();
        
        Booking booking = new Booking(
            "Http",
            "Quality",
            500,
            true,
            new BookingDates("2026-12-20", "2026-12-25"),
            "Breakfast"
        );

        Response response = bookingClient.createBooking(booking);

        Assert.assertEquals(
            response.statusCode(),
            200,
            "Fixture booking creation failed: " + response.asString()
        );

        bookingId = response.jsonPath().getInt("bookingid");

        Assert.assertTrue(
            bookingId > 0,
            "Invalid booking ID returned: " + bookingId
        );

    }

    // =========================================================
    // Content negotiation
    // =========================================================

    @Test(groups={"regression","smoke","content"})
    public void getBookingWithJsonAcceptReturnsJson() {

        Response response =
                bookingClient.getBookingWithAccept(
                        bookingId,
                        "application/json");

        Assert.assertEquals(
                response.statusCode(),
                200,
                "Expected HTTP 200 but received "
                        + response.statusCode()
                        + ": "
                        + response.asString());

        Assert.assertTrue(
                response.contentType()
                        .toLowerCase()
                        .contains("application/json"),
                "Expected JSON response but received "
                        + response.contentType());

        Assert.assertTrue(
                response.asString()
                        .contains("\"firstname\""),
                "Expected JSON booking representation");
    }

    @Test(groups={"regression","content"})
    public void getBookingWithXmlAcceptReturnsXml() {

        Response response = bookingClient.getBookingWithAccept(
                bookingId,
                "application/xml"
        );

        Assert.assertEquals(
                response.getStatusCode(),
                200,
                "Expected HTTP 200 for XML GET"
        );

        String body = response.getBody().asString();

        Assert.assertTrue(
                body.trim().startsWith("<?xml"),
                "Expected XML response body but received: " + body
        );

        Assert.assertTrue(
                body.contains("<booking>"),
                "Expected <booking> element in XML response"
        );

        Assert.assertTrue(
                body.contains("<firstname>"),
                "Expected <firstname> element in XML response"
        );

        Assert.assertTrue(
                body.contains("<lastname>"),
                "Expected <lastname> element in XML response"
        );

        /*
         * Characterization:
         * Restful Booker currently returns an XML body but may incorrectly
         * label the response Content-Type as text/html.
         *
         * We therefore validate the actual response behavior rather than
         * assuming the server's Content-Type header is correct.
         */
        String contentType = response.getContentType();

        Assert.assertTrue(
                contentType.contains("text/html")
                        || contentType.contains("application/xml")
                        || contentType.contains("text/xml"),
                "Unexpected Content-Type: " + contentType
        );
    }

    @Test(groups={"regression","content"})
    public void createBookingWithJsonContentTypeAndJsonAccept() {

        String payload = """
                {
                  "firstname": "Negotiation",
                  "lastname": "Json",
                  "totalprice": 500,
                  "depositpaid": true,
                  "bookingdates": {
                    "checkin": "2026-12-20",
                    "checkout": "2026-12-25"
                  },
                  "additionalneeds": "Breakfast"
                }
                """;

        Response response =
                bookingClient.createBookingWithContentType(
                        payload,
                        "application/json",
                        "application/json");

        Assert.assertEquals(
                response.statusCode(),
                200);

        Assert.assertTrue(
                response.contentType()
                        .toLowerCase()
                        .contains("application/json"),
                "Expected JSON response but received "
                        + response.contentType());

        Assert.assertNotNull(
                response.jsonPath().get("bookingid"),
                "Expected bookingid in response");
    }

    @Test(groups={"regression","content"})
    public void createBookingWithXmlContentTypeAndXmlAccept() {

        String payload = """
                <booking>
                  <firstname>Negotiation</firstname>
                  <lastname>Xml</lastname>
                  <totalprice>500</totalprice>
                  <depositpaid>true</depositpaid>
                  <bookingdates>
                    <checkin>2026-12-20</checkin>
                    <checkout>2026-12-25</checkout>
                  </bookingdates>
                  <additionalneeds>Breakfast</additionalneeds>
                </booking>
                """;

        Response response =
                bookingClient.createBookingWithContentType(
                        payload,
                        "text/xml",
                        "application/xml");

        /*
         * The deployed public API has demonstrated HTTP 500
         * for XML booking creation.
         *
         * This is a characterization test of the current API
         * behavior, not a statement that 500 is desirable.
         */
        Assert.assertEquals(
                response.statusCode(),
                500,
                "Unexpected XML creation response: "
                        + response.asString());
    }

    // =========================================================
    // Basic response-time assertion
    // =========================================================

    @Test(groups={"regression"})
    public void getBookingRespondsWithinReasonableTime() {

        Response response =
                bookingClient.getBooking(bookingId);

        Assert.assertEquals(
                response.statusCode(),
                200);

        /*
         * This is intentionally generous because the test
         * targets a public internet service.
         *
         * This is a smoke-level performance guard,
         * NOT a performance test.
         */
        Assert.assertTrue(
                response.getTime() < 5000,
                "GET /booking/{id} exceeded 5 seconds: "
                        + response.getTime()
                        + " ms");
    }

    @Test(groups={"regression"})
    public void listBookingsRespondsWithinReasonableTime() {

        Response response =
                bookingClient.listBookings();

        Assert.assertEquals(
                response.statusCode(),
                200);

        Assert.assertTrue(
                response.getTime() < 5000,
                "GET /booking exceeded 5 seconds: "
                        + response.getTime()
                        + " ms");
    }

    // =========================================================
    // Response headers
    // =========================================================

    @Test(groups={"regression","content"})
    public void getBookingReturnsContentTypeHeader() {

        Response response =
                bookingClient.getBooking(bookingId);

        Assert.assertNotNull(
                response.getHeader("Content-Type"),
                "Content-Type header should be present");
    }

    @Test(groups={"regression","content"})
    public void createBookingReturnsContentTypeHeader() {

        Booking booking = new Booking(
                "Header",
                "Validation",
                300,
                false,
                new BookingDates(
                        "2026-12-20",
                        "2026-12-25"),
                "None");

        Response response =
                bookingClient.createBooking(booking);

        Assert.assertEquals(
                response.statusCode(),
                200);

        Assert.assertNotNull(
                response.getHeader("Content-Type"),
                "Content-Type header should be present");
    }

    @Test(groups={"regression","content"})
    public void getBookingReturnsJsonContentTypeByDefault() {

        Response response =
                bookingClient.getBooking(bookingId);

        Assert.assertTrue(
                response.contentType()
                        .toLowerCase()
                        .contains("application/json"),
                "Unexpected Content-Type: "
                        + response.contentType());
    }
}