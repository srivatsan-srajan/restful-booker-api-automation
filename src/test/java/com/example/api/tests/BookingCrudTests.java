package com.example.api.tests;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.example.api.base.BaseTest;
import com.example.api.models.Booking;
import com.example.api.models.BookingDates;
import com.example.api.payloads.BookingPayload;

import io.restassured.response.Response;

public class BookingCrudTests extends BaseTest {

    /*
     * Dedicated booking for the stateful CRUD flow.
     *
     * POST
     *  -> PUT
     *  -> Verify PUT
     *  -> PATCH
     *  -> Verify PATCH
     *  -> DELETE
     *  -> Verify DELETE
     */
    private int bookingId;

    @BeforeClass
    public void createFixture() {

        authenticate();

        Booking booking = new Booking(
                "Srivatsan",
                "Automation",
                1250,
                true,
                new BookingDates(
                        "2026-10-01",
                        "2026-10-05"),
                "Breakfast");

        Response response =
                bookingClient.createBooking(booking);

        Assert.assertEquals(
                response.statusCode(),
                200,
                "Fixture creation failed: "
                        + response.asString());

        bookingId =
                response.jsonPath().getInt("bookingid");

        Assert.assertTrue(
                bookingId > 0,
                "Invalid fixture booking ID");
    }

    /*
     * Creates isolated data for independent tests.
     */
    private int createIndependentBooking(
            String firstname,
            String lastname) {

        Booking booking = new Booking(
                firstname,
                lastname,
                1000,
                true,
                new BookingDates(
                        "2026-10-10",
                        "2026-10-15"),
                "Breakfast");

        Response response =
                bookingClient.createBooking(booking);

        Assert.assertEquals(
                response.statusCode(),
                200,
                "Independent booking creation failed: "
                        + response.asString());

        int id =
                response.jsonPath().getInt("bookingid");

        Assert.assertTrue(
                id > 0,
                "Invalid independent booking ID");

        return id;
    }

    // =========================================================
    // CREATE
    // =========================================================

    @Test(groups={"regression","smoke"})
    public void createBookingReturnsCompleteBookingData() {

        Booking booking = new Booking(
                "Create",
                "Validation",
                100,
                false,
                new BookingDates(
                        "2026-09-10",
                        "2026-09-12"),
                "None");

        bookingClient
                .createBooking(booking)
                .then()
                .statusCode(200)
                .body(
                        "bookingid",
                        notNullValue())
                .body(
                        "booking.firstname",
                        equalTo("Create"))
                .body(
                        "booking.lastname",
                        equalTo("Validation"))
                .body(
                        "booking.totalprice",
                        equalTo(100))
                .body(
                        "booking.depositpaid",
                        equalTo(false))
                .body(
                        "booking.bookingdates.checkin",
                        equalTo("2026-09-10"))
                .body(
                        "booking.bookingdates.checkout",
                        equalTo("2026-09-12"))
                .body(
                        "booking.additionalneeds",
                        equalTo("None"));
    }

    @Test(groups={"regression"})
    public void createBookingPersistsAndCanBeRetrieved() {

        Booking booking = new Booking(
                "Post",
                "Persistence",
                750,
                true,
                new BookingDates(
                        "2026-09-20",
                        "2026-09-25"),
                "Breakfast");

        Response createResponse =
                bookingClient.createBooking(booking);

        createResponse
                .then()
                .statusCode(200)
                .body(
                        "bookingid",
                        notNullValue());

        int createdId =
                createResponse
                        .jsonPath()
                        .getInt("bookingid");

        bookingClient
                .getBooking(createdId)
                .then()
                .statusCode(200)
                .body(
                        "firstname",
                        equalTo("Post"))
                .body(
                        "lastname",
                        equalTo("Persistence"))
                .body(
                        "totalprice",
                        equalTo(750))
                .body(
                        "depositpaid",
                        equalTo(true))
                .body(
                        "bookingdates.checkin",
                        equalTo("2026-09-20"))
                .body(
                        "bookingdates.checkout",
                        equalTo("2026-09-25"))
                .body(
                        "additionalneeds",
                        equalTo("Breakfast"));
    }

    @Test(groups={"regression"})
    public void createBookingWithJsonPayload() {

        bookingClient
                .createBookingJson(
                        BookingPayload.validBookingJson())
                .then()
                .statusCode(200)
                .body(
                        "bookingid",
                        notNullValue());
    }

    @Test(groups={"regression","negative"})
    public void createBookingMissingFirstname() {

        Response response =
                bookingClient.createBookingJson("""
                    {
                      "lastname": "Automation",
                      "totalprice": 100,
                      "depositpaid": true,
                      "bookingdates": {
                        "checkin": "2026-10-01",
                        "checkout": "2026-10-05"
                      }
                    }
                    """);

        /*
         * Observed current API behavior:
         * missing firstname is accepted at HTTP level
         * and returns 500 from the deployed application.
         */
        Assert.assertEquals(
                response.statusCode(),
                500,
                "Unexpected API behavior: "
                        + response.asString());
    }

    @Test(groups={"regression","content"})
    public void createBookingUsingXml() {

        Response response =
                bookingClient.createBookingXml(
                        BookingPayload.xmlBooking());

        /*
         * Current deployed API behavior.
         */
        Assert.assertEquals(
                response.statusCode(),
                500,
                "Unexpected XML response: "
                        + response.asString());
    }

    @Test(groups={"regression","content"})
    public void createBookingUsingUrlEncodedForm() {

        Booking booking = new Booking(
                "Form",
                "Automation",
                200,
                true,
                new BookingDates(
                        "2026-11-10",
                        "2026-11-12"),
                "Breakfast");

        bookingClient
                .createBookingUrlEncoded(booking)
                .then()
                .statusCode(200)
                .body(
                        "bookingid",
                        notNullValue());
    }

    // =========================================================
    // PUT - COOKIE AUTH
    // =========================================================

    @Test(groups={"regression"})
    public void putBookingWithCookieAuthentication() {

        bookingClient
                .putBooking(
                        bookingId,
                        token,
                        new Booking(
                                "Put",
                                "Updated",
                                1500,
                                true,
                                new BookingDates(
                                        "2026-10-02",
                                        "2026-10-06"),
                                "Breakfast and WiFi"))
                .then()
                .statusCode(200)
                .body(
                        "firstname",
                        equalTo("Put"))
                .body(
                        "lastname",
                        equalTo("Updated"))
                .body(
                        "totalprice",
                        equalTo(1500))
                .body(
                        "depositpaid",
                        equalTo(true))
                .body(
                        "bookingdates.checkin",
                        equalTo("2026-10-02"))
                .body(
                        "bookingdates.checkout",
                        equalTo("2026-10-06"))
                .body(
                        "additionalneeds",
                        equalTo("Breakfast and WiFi"));
    }

    @Test(groups={"regression"}, dependsOnMethods="putBookingWithCookieAuthentication")
    public void verifyPutPersistence() {

        bookingClient
                .getBooking(bookingId)
                .then()
                .statusCode(200)
                .body(
                        "firstname",
                        equalTo("Put"))
                .body(
                        "lastname",
                        equalTo("Updated"))
                .body(
                        "totalprice",
                        equalTo(1500))
                .body(
                        "depositpaid",
                        equalTo(true))
                .body(
                        "bookingdates.checkin",
                        equalTo("2026-10-02"))
                .body(
                        "bookingdates.checkout",
                        equalTo("2026-10-06"))
                .body(
                        "additionalneeds",
                        equalTo("Breakfast and WiFi"));
    }

    @Test(groups={"regression","negative"})
    public void putWithInvalidTokenIsRejected() {

        Response response =
                bookingClient.putBooking(
                        bookingId,
                        "invalid-token",
                        new Booking(
                                "Invalid",
                                "Token",
                                1,
                                true,
                                new BookingDates(
                                        "2026-10-01",
                                        "2026-10-02"),
                                "None"));

        Assert.assertTrue(
                response.statusCode() >= 400,
                "Expected authentication failure but received: "
                        + response.statusCode());
    }

    // =========================================================
    // PATCH
    // =========================================================

    @Test(groups={"regression"}, dependsOnMethods="verifyPutPersistence")
    public void patchBookingWithCookieAuthentication() {

        bookingClient
                .patchBooking(
                        bookingId,
                        token,
                        BookingPayload.patchAdditionalNeedsJson())
                .then()
                .statusCode(200)
                .body(
                        "additionalneeds",
                        equalTo("Airport pickup"))
                .body(
                        "firstname",
                        equalTo("Put"))
                .body(
                        "lastname",
                        equalTo("Updated"));
    }

    @Test(groups={"regression"}, dependsOnMethods="patchBookingWithCookieAuthentication")
    public void verifyPatchPersistence() {

        bookingClient
                .getBooking(bookingId)
                .then()
                .statusCode(200)
                .body(
                        "additionalneeds",
                        equalTo("Airport pickup"))
                .body(
                        "firstname",
                        equalTo("Put"))
                .body(
                        "lastname",
                        equalTo("Updated"))
                .body(
                        "totalprice",
                        equalTo(1500));
    }

    @Test(groups={"regression"})
    public void patchOnlyChangesRequestedField() {

        int patchBookingId =
                createIndependentBooking(
                        "Patch",
                        "Isolation");

        bookingClient
                .patchBooking(
                        patchBookingId,
                        token,
                        "{\"firstname\":\"Patched\"}")
                .then()
                .statusCode(200)
                .body(
                        "firstname",
                        equalTo("Patched"))
                .body(
                        "lastname",
                        equalTo("Isolation"))
                .body(
                        "totalprice",
                        equalTo(1000))
                .body(
                        "depositpaid",
                        equalTo(true))
                .body(
                        "additionalneeds",
                        equalTo("Breakfast"));
    }

    @Test(groups={"regression","negative"})
    public void patchWithInvalidTokenIsRejected() {

        Response response =
                bookingClient.patchBooking(
                        bookingId,
                        "invalid-token",
                        "{\"additionalneeds\":\"Invalid token\"}");

        Assert.assertTrue(
                response.statusCode() >= 400,
                "Expected authentication failure but received: "
                        + response.statusCode());
    }

    // =========================================================
    // BASIC AUTH
    // =========================================================

    @Test(groups={"regression"})
    public void putWithBasicAuthentication() {

        int basicAuthBookingId =
                createIndependentBooking(
                        "Basic",
                        "Auth");

        bookingClient
                .putBookingBasicAuth(
                        basicAuthBookingId,
                        new Booking(
                                "Basic",
                                "AuthUpdated",
                                1600,
                                true,
                                new BookingDates(
                                        "2026-10-03",
                                        "2026-10-07"),
                                "Breakfast"))
                .then()
                .statusCode(200)
                .body(
                        "firstname",
                        equalTo("Basic"))
                .body(
                        "lastname",
                        equalTo("AuthUpdated"))
                .body(
                        "totalprice",
                        equalTo(1600));
    }

    @Test(groups={"regression"})
    public void patchWithBasicAuthentication() {

        int basicAuthBookingId =
                createIndependentBooking(
                        "Patch",
                        "Basic");

        bookingClient
                .patchBookingBasicAuth(
                        basicAuthBookingId,
                        "{\"additionalneeds\":\"Late checkout\"}")
                .then()
                .statusCode(200)
                .body(
                        "firstname",
                        equalTo("Patch"))
                .body(
                        "additionalneeds",
                        equalTo("Late checkout"));
    }

    // =========================================================
    // NO AUTH
    // =========================================================

    @Test(groups={"regression","negative"})
    public void updateWithoutAuthenticationIsRejected() {

        Response response =
                bookingClient.putBookingWithoutAuth(
                        bookingId,
                        new Booking(
                                "No",
                                "Auth",
                                1,
                                true,
                                new BookingDates(
                                        "2026-10-01",
                                        "2026-10-02"),
                                "None"));

        Assert.assertEquals(
                response.statusCode(),
                403,
                "Unexpected unauthenticated PUT response: "
                        + response.asString());
    }

    @Test(groups={"regression","negative"})
    public void patchWithoutAuthenticationIsRejected() {

        Response response =
                bookingClient.patchBookingWithoutAuth(
                        bookingId,
                        "{\"additionalneeds\":\"No auth\"}");

        Assert.assertTrue(
                response.statusCode() >= 400,
                "Expected authentication failure but received: "
                        + response.statusCode());
    }

    @Test(groups={"regression","negative"})
    public void deleteWithoutAuthenticationIsRejected() {

        Response response =
                bookingClient.deleteBookingWithoutAuth(
                        bookingId);

        Assert.assertEquals(
                response.statusCode(),
                403,
                "Unexpected unauthenticated DELETE response: "
                        + response.asString());
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Test(groups={"regression"}, dependsOnMethods="verifyPatchPersistence")
    public void deleteBookingWithToken() {

        bookingClient
                .deleteBooking(
                        bookingId,
                        token)
                .then()
                .statusCode(201);
    }

    @Test(groups={"regression"}, dependsOnMethods="deleteBookingWithToken")
    public void verifyDeleteReturns404() {

        bookingClient
                .getBooking(bookingId)
                .then()
                .statusCode(404);
    }

    @Test(groups={"regression","negative"})
    public void deleteNonExistingBookingReturnsError() {

        Response response =
                bookingClient.deleteBooking(
                        99999999,
                        token);

        Assert.assertTrue(
                response.statusCode() >= 400,
                "Expected error for nonexistent booking but received: "
                        + response.statusCode());
    }

    // =========================================================
    // INVALID IDs
    // =========================================================

    @Test(groups={"regression","negative"})
    public void getInvalidBookingIdReturns404() {

        bookingClient
                .getBooking(-1)
                .then()
                .statusCode(404);
    }

    @Test(groups={"regression","negative"})
    public void getZeroBookingIdReturns404() {

        bookingClient
                .getBooking(0)
                .then()
                .statusCode(404);
    }

    @Test(groups={"regression","negative"})
    public void getNonExistingBookingIdReturns404() {

        bookingClient
                .getBooking(99999999)
                .then()
                .statusCode(404);
    }
}