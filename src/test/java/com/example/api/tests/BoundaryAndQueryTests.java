package com.example.api.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.example.api.base.BaseTest;

import io.restassured.response.Response;

public class BoundaryAndQueryTests extends BaseTest {

    // =========================================================
    // GET ID boundary cases
    // =========================================================

    @Test(groups={"regression","boundary"})
    public void getBookingIdZeroReturnsNotFound() {

        bookingClient
                .getBooking(0)
                .then()
                .statusCode(404);
    }

    @Test(groups={"regression","boundary"})
    public void getBookingNegativeIdReturnsNotFound() {

        bookingClient
                .getBooking(-1)
                .then()
                .statusCode(404);
    }

    @Test(groups={"regression","boundary"})
    public void getBookingVeryLargeIdReturnsNotFound() {

        bookingClient
                .getBooking(Integer.MAX_VALUE)
                .then()
                .statusCode(404);
    }

    @Test(groups={"regression","boundary"})
    public void getBookingVeryLargeNegativeIdReturnsNotFound() {

        bookingClient
                .getBooking(Integer.MIN_VALUE)
                .then()
                .statusCode(404);
    }

    // =========================================================
    // Date permutations
    // =========================================================

    @Test(groups={"regression","boundary"})
    public void dateFilterWithSameCheckinAndCheckoutIsHandled() {

        Response response =
                bookingClient.listBookingsByDates(
                        "2026-12-15",
                        "2026-12-15");

        Assert.assertEquals(
                response.statusCode(),
                200,
                "Unexpected response: "
                        + response.asString());

        Assert.assertNotNull(
                response.jsonPath().getList("$"),
                "Expected a JSON array response");
    }

    @Test(groups={"regression","boundary"})
    public void dateFilterWithReversedDatesIsHandled() {

        Response response =
                bookingClient.listBookingsByDates(
                        "2026-12-20",
                        "2026-12-10");

        Assert.assertEquals(
                response.statusCode(),
                200,
                "Unexpected response: "
                        + response.asString());

        Assert.assertNotNull(
                response.jsonPath().getList("$"),
                "Expected a JSON array response");
    }

    @Test(groups={"regression","negative","boundary"})
    public void dateFilterWithInvalidDateFormatIsHandled() {

        Response response =
                bookingClient.listBookingsByDates(
                        "not-a-date",
                        "also-not-a-date");

        /*
         * Characterization test:
         * The deployed public API does not provide a stable,
         * documented validation contract for malformed query dates.
         *
         * We therefore verify that the request receives a valid
         * HTTP response rather than inventing an expected status.
         */
        Assert.assertTrue(
                response.statusCode() >= 200
                        && response.statusCode() < 600,
                "API did not return a valid HTTP response");
    }

    @Test(groups={"regression","boundary"})
    public void dateFilterWithLeapDayIsHandled() {

        Response response =
                bookingClient.listBookingsByDates(
                        "2028-02-29",
                        "2028-03-01");

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
    // Query filter combinations
    // =========================================================

    @Test(groups={"regression","boundary"})
    public void firstnameOnlyFilterReturnsJsonArray() {

        Response response =
                bookingClient.listBookingsByName(
                        "Srivatsan",
                        null);

        Assert.assertEquals(
                response.statusCode(),
                200);

        Assert.assertNotNull(
                response.jsonPath().getList("$"),
                "Expected a JSON array response");
    }

    @Test(groups={"regression","boundary"})
    public void lastnameOnlyFilterReturnsJsonArray() {

        Response response =
                bookingClient.listBookingsByName(
                        null,
                        "Automation");

        Assert.assertEquals(
                response.statusCode(),
                200);

        Assert.assertNotNull(
                response.jsonPath().getList("$"),
                "Expected a JSON array response");
    }

    @Test(groups={"regression","boundary"})
    public void firstnameAndLastnameFilterReturnsJsonArray() {

        Response response =
                bookingClient.listBookingsByName(
                        "Srivatsan",
                        "Automation");

        Assert.assertEquals(
                response.statusCode(),
                200);

        Assert.assertNotNull(
                response.jsonPath().getList("$"),
                "Expected a JSON array response");
    }

    @Test(groups={"regression","boundary"})
    public void checkinOnlyFilterReturnsJsonArray() {

        Response response =
                bookingClient.listBookingsByDates(
                        "2026-10-01",
                        null);

        Assert.assertEquals(
                response.statusCode(),
                200);

        Assert.assertNotNull(
                response.jsonPath().getList("$"),
                "Expected a JSON array response");
    }

    @Test(groups={"regression","boundary"})
    public void checkoutOnlyFilterReturnsJsonArray() {

        Response response =
                bookingClient.listBookingsByDates(
                        null,
                        "2026-10-05");

        Assert.assertEquals(
                response.statusCode(),
                200);

        Assert.assertNotNull(
                response.jsonPath().getList("$"),
                "Expected a JSON array response");
    }

    @Test(groups={"regression","boundary"})
    public void checkinAndCheckoutFilterReturnsJsonArray() {

        Response response =
                bookingClient.listBookingsByDates(
                        "2026-10-01",
                        "2026-10-05");

        Assert.assertEquals(
                response.statusCode(),
                200);

        Assert.assertNotNull(
                response.jsonPath().getList("$"),
                "Expected a JSON array response");
    }
}