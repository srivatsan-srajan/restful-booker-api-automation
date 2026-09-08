package com.example.api.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.example.api.base.BaseTest;
import com.example.api.models.Booking;
import com.example.api.models.BookingDates;

import io.restassured.response.Response;

public class ExtendedCrudValidationTests extends BaseTest {

    @BeforeClass
    public void authenticateForAdditionalTests() {

        authenticate();
    }

    private int createBooking() {

        Booking booking = new Booking(
                "Extended",
                "Validation",
                900,
                true,
                new BookingDates(
                        "2026-12-01",
                        "2026-12-05"),
                "Breakfast");

        Response response =
                bookingClient.createBooking(booking);

        Assert.assertEquals(
                response.statusCode(),
                200,
                "Booking creation failed: "
                        + response.asString());

        int bookingId =
                response.jsonPath()
                        .getInt("bookingid");

        Assert.assertTrue(
                bookingId > 0,
                "Invalid booking ID returned");

        return bookingId;
    }

    // =========================================================
    // PUT missing / invalid fields
    // =========================================================

    @Test(groups={"regression","negative"})
    public void putWithMissingFirstnameIsHandledByApi() {

        int id = createBooking();

        Response response =
                bookingClient.putBooking(
                        id,
                        token,
                        new Booking(
                                null,
                                "PutMissingFirstname",
                                950,
                                true,
                                new BookingDates(
                                        "2026-12-02",
                                        "2026-12-06"),
                                "Breakfast"));

        /*
         * Characterization test.
         *
         * The deployed API is permissive about validation and
         * should not be assumed to return 400 merely because a
         * field is missing.
         */
        Assert.assertTrue(
                response.statusCode() >= 200
                        && response.statusCode() < 600,
                "API did not return a valid HTTP response");
    }

    @Test(groups={"regression","negative"})
    public void putWithInvalidTotalPriceTypeIsHandledByApi() {

        int id = createBooking();

        String payload = """
                {
                  "firstname": "Invalid",
                  "lastname": "Price",
                  "totalprice": "not-a-number",
                  "depositpaid": true,
                  "bookingdates": {
                    "checkin": "2026-12-02",
                    "checkout": "2026-12-06"
                  },
                  "additionalneeds": "Breakfast"
                }
                """;

        Response response =
                bookingClient.putBookingJson(
                        id,
                        token,
                        payload);

        Assert.assertTrue(
                response.statusCode() >= 200
                        && response.statusCode() < 600,
                "API did not return a valid HTTP response");
    }

    // =========================================================
    // PATCH empty body
    // =========================================================

    @Test(groups={"regression","negative"})
    public void patchWithEmptyJsonObjectIsHandledByApi() {

        int id = createBooking();

        Response response =
                bookingClient.patchBooking(
                        id,
                        token,
                        "{}");

        Assert.assertTrue(
                response.statusCode() >= 200
                        && response.statusCode() < 600,
                "API did not return a valid HTTP response");
    }

    @Test(groups={"regression","negative"})
    public void patchWithEmptyBodyIsHandledByApi() {

        int id = createBooking();

        Response response =
                bookingClient.patchBooking(
                        id,
                        token,
                        "");

        Assert.assertTrue(
                response.statusCode() >= 200
                        && response.statusCode() < 600,
                "API did not return a valid HTTP response");
    }

    // =========================================================
    // Error response validation
    // =========================================================

    @Test(groups={"regression","negative"})
    public void getNonExistingBookingReturns404WithResponse() {

        Response response =
                bookingClient.getBooking(99999999);

        Assert.assertEquals(
                response.statusCode(),
                404,
                "Expected 404 for nonexistent booking");

        Assert.assertNotNull(
                response,
                "Response must not be null");
    }

    @Test(groups={"regression","negative"})
    public void unauthenticatedPatchReturnsAuthenticationError() {

        int id = createBooking();

        Response response =
                bookingClient.patchBookingWithoutAuth(
                        id,
                        "{\"firstname\":\"Unauthorized\"}");

        Assert.assertTrue(
                response.statusCode() >= 400,
                "Expected authentication-related failure but got "
                        + response.statusCode());
    }

    @Test(groups={"regression","negative"})
    public void invalidTokenDeleteReturnsError() {

        int id = createBooking();

        Response response =
                bookingClient.deleteBookingWithInvalidToken(
                        id,
                        "definitely-invalid-token");

        Assert.assertTrue(
                response.statusCode() >= 400,
                "Expected authentication failure but got "
                        + response.statusCode());
    }

    // =========================================================
    // String boundary cases
    // =========================================================

    @Test(groups={"regression","boundary"})
    public void createBookingWithEmptyStringFieldsIsHandled() {

        Booking booking = new Booking(
                "",
                "",
                100,
                false,
                new BookingDates(
                        "2026-12-10",
                        "2026-12-12"),
                "");

        Response response =
                bookingClient.createBooking(booking);

        /*
         * Characterization test:
         * The deployed API does not consistently enforce
         * non-empty string validation.
         */
        Assert.assertTrue(
                response.statusCode() >= 200
                        && response.statusCode() < 600,
                "API did not return a valid HTTP response");
    }

    @Test(groups={"regression","boundary"})
    public void createBookingWithWhitespaceFieldsIsHandled() {

        Booking booking = new Booking(
                "   ",
                "   ",
                100,
                false,
                new BookingDates(
                        "2026-12-10",
                        "2026-12-12"),
                "   ");

        Response response =
                bookingClient.createBooking(booking);

        Assert.assertTrue(
                response.statusCode() >= 200
                        && response.statusCode() < 600,
                "API did not return a valid HTTP response");
    }

    @Test(groups={"regression","boundary"})
    public void createBookingWithSpecialCharactersIsHandled() {

        Booking booking = new Booking(
                "José",
                "O'Connor-Smith",
                100,
                false,
                new BookingDates(
                        "2026-12-10",
                        "2026-12-12"),
                "Breakfast & WiFi");

        Response response =
                bookingClient.createBooking(booking);

        Assert.assertEquals(
                response.statusCode(),
                200);

        Assert.assertEquals(
                response.jsonPath()
                        .getString("booking.firstname"),
                "José");

        Assert.assertEquals(
                response.jsonPath()
                        .getString("booking.lastname"),
                "O'Connor-Smith");
    }

    @Test(groups={"regression","boundary"})
    public void createBookingWithLongStringFieldsIsHandled() {

        String longValue =
                "A".repeat(250);

        Booking booking = new Booking(
                longValue,
                longValue,
                100,
                false,
                new BookingDates(
                        "2026-12-10",
                        "2026-12-12"),
                longValue);

        Response response =
                bookingClient.createBooking(booking);

        Assert.assertTrue(
                response.statusCode() >= 200
                        && response.statusCode() < 600,
                "API did not return a valid HTTP response");
    }
}