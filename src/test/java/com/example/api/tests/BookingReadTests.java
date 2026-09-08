package com.example.api.tests;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.example.api.base.BaseTest;
import com.example.api.models.Booking;
import com.example.api.models.BookingDates;

import io.restassured.response.Response;

public class BookingReadTests extends BaseTest {

    private int createBookingForReadTest() {

        Booking booking = new Booking(
                "ReadTest",
                "Automation",
                850,
                true,
                new BookingDates(
                        "2026-09-20",
                        "2026-09-25"),
                "Breakfast");

        Response response =
                bookingClient.createBooking(booking);

        Assert.assertEquals(
                response.statusCode(),
                200,
                "Booking creation failed: "
                        + response.asString());

        int id =
                response.jsonPath().getInt("bookingid");

        Assert.assertTrue(
                id > 0,
                "Invalid booking ID returned");

        return id;
    }

    // =========================================================
    // GET /booking
    // =========================================================

    @Test(groups={"regression","smoke"})
    public void listAllBookingsReturnsBookingIds() {

        bookingClient
                .listBookings()
                .then()
                .statusCode(200)
                .body("$", notNullValue())
                .body("bookingid.size()", greaterThan(0));
    }

    // =========================================================
    // NAME FILTERING
    // =========================================================

    @Test(groups={"regression"})
    public void filterByFirstnameReturnsSuccessfulResponse() {

        Response response =
                bookingClient.listBookingsByName(
                        "ReadTest",
                        null);

        Assert.assertEquals(
                response.statusCode(),
                200,
                "Firstname filter failed: "
                        + response.asString());

        List<?> bookings =
                response.jsonPath().getList("$");

        Assert.assertNotNull(
                bookings,
                "Expected a JSON array response");
    }

    @Test(groups={"regression"})
    public void filterByLastnameReturnsSuccessfulResponse() {

        Response response =
                bookingClient.listBookingsByName(
                        null,
                        "Automation");

        Assert.assertEquals(
                response.statusCode(),
                200,
                "Lastname filter failed: "
                        + response.asString());

        List<?> bookings =
                response.jsonPath().getList("$");

        Assert.assertNotNull(
                bookings,
                "Expected a JSON array response");
    }

    @Test(groups={"regression"})
    public void filterByFirstnameAndLastnameReturnsSuccessfulResponse() {

        Response response =
                bookingClient.listBookingsByName(
                        "ReadTest",
                        "Automation");

        Assert.assertEquals(
                response.statusCode(),
                200,
                "Combined name filter failed: "
                        + response.asString());

        List<?> bookings =
                response.jsonPath().getList("$");

        Assert.assertNotNull(
                bookings,
                "Expected a JSON array response");
    }

    @Test(groups={"regression","negative"})
    public void filterByNonExistingFirstnameReturnsEmptyResult() {

        Response response =
                bookingClient.listBookingsByName(
                        "DefinitelyDoesNotExist_999999",
                        null);

        Assert.assertEquals(
                response.statusCode(),
                200,
                "Unexpected response: "
                        + response.asString());

        List<?> bookings =
                response.jsonPath().getList("$");

        Assert.assertNotNull(
                bookings,
                "Expected a JSON array response");

        Assert.assertEquals(
                bookings.size(),
                0,
                "Expected no bookings for nonexistent firstname");
    }

    @Test(groups={"regression","negative"})
    public void filterByNonExistingLastnameReturnsEmptyResult() {

        Response response =
                bookingClient.listBookingsByName(
                        null,
                        "DefinitelyDoesNotExist_999999");

        Assert.assertEquals(
                response.statusCode(),
                200,
                "Unexpected response: "
                        + response.asString());

        List<?> bookings =
                response.jsonPath().getList("$");

        Assert.assertNotNull(
                bookings,
                "Expected a JSON array response");

        Assert.assertEquals(
                bookings.size(),
                0,
                "Expected no bookings for nonexistent lastname");
    }

    // =========================================================
    // DATE FILTERING
    // =========================================================

    @Test(groups={"regression"})
    public void filterByDateRangeReturnsSuccessfulResponse() {

        Response response =
                bookingClient.listBookingsByDates(
                        "2026-09-20",
                        "2026-09-25");

        Assert.assertEquals(
                response.statusCode(),
                200,
                "Date filter failed: "
                        + response.asString());

        Assert.assertNotNull(
                response.jsonPath().getList("$"),
                "Expected a JSON array response");
    }

    @Test(groups={"regression"})
    public void filterByCheckinOnlyReturnsSuccessfulResponse() {

        Response response =
                bookingClient.listBookingsByDates(
                        "2026-09-20",
                        null);

        Assert.assertEquals(
                response.statusCode(),
                200,
                "Checkin filter failed: "
                        + response.asString());

        Assert.assertNotNull(
                response.jsonPath().getList("$"),
                "Expected a JSON array response");
    }

    @Test(groups={"regression"})
    public void filterByCheckoutOnlyReturnsSuccessfulResponse() {

        Response response =
                bookingClient.listBookingsByDates(
                        null,
                        "2026-09-25");

        Assert.assertEquals(
                response.statusCode(),
                200,
                "Checkout filter failed: "
                        + response.asString());

        Assert.assertNotNull(
                response.jsonPath().getList("$"),
                "Expected a JSON array response");
    }

    @Test(groups={"regression"})
    public void filterByFutureDateRangeReturnsSuccessfulResponse() {

        Response response =
                bookingClient.listBookingsByDates(
                        "2099-01-01",
                        "2099-01-02");

        Assert.assertEquals(
                response.statusCode(),
                200,
                "Unexpected response: "
                        + response.asString());

        Assert.assertNotNull(
                response.jsonPath().getList("$"),
                "Expected a JSON array response");
    }

    // =========================================================
    // GET /booking/{id}
    // =========================================================

    @Test(groups={"regression"})
    public void getExistingBookingReturnsCompleteRepresentation() {

        int bookingId =
                createBookingForReadTest();

        Response response =
                bookingClient.getBooking(bookingId);

        /*
         * The public Restful Booker environment can occasionally
         * lose newly-created records between requests.
         *
         * The test therefore validates the representation only
         * when the booking is still available.
         */
        if (response.statusCode() == 404) {
            Assert.fail(
                    "Booking was created successfully but was no longer "
                    + "available for GET /booking/{id}. Booking ID: "
                    + bookingId);
        }

        Assert.assertEquals(
                response.statusCode(),
                200,
                "Unexpected GET response: "
                        + response.asString());

        Assert.assertNotNull(
                response.jsonPath().getString("firstname"),
                "firstname should be present");

        Assert.assertNotNull(
                response.jsonPath().getString("lastname"),
                "lastname should be present");

        Assert.assertNotNull(
                response.jsonPath().get("totalprice"),
                "totalprice should be present");

        Assert.assertNotNull(
                response.jsonPath().get("depositpaid"),
                "depositpaid should be present");

        Assert.assertNotNull(
                response.jsonPath().get("bookingdates"),
                "bookingdates should be present");

        Assert.assertNotNull(
                response.jsonPath().getString(
                        "bookingdates.checkin"),
                "checkin should be present");

        Assert.assertNotNull(
                response.jsonPath().getString(
                        "bookingdates.checkout"),
                "checkout should be present");
    }

    @Test(groups={"regression","negative","boundary"})
    public void getBookingZeroReturns404() {

        bookingClient
                .getBooking(0)
                .then()
                .statusCode(404);
    }

    @Test(groups={"regression","negative","boundary"})
    public void getNegativeBookingIdReturns404() {

        bookingClient
                .getBooking(-1)
                .then()
                .statusCode(404);
    }

    @Test(groups={"regression","negative","boundary"})
    public void getVeryLargeNonExistingBookingIdReturns404() {

        bookingClient
                .getBooking(99999999)
                .then()
                .statusCode(404);
    }
}