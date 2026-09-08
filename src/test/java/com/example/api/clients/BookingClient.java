package com.example.api.clients;

import static io.restassured.RestAssured.given;

import com.example.api.config.ConfigManager;
import com.example.api.models.Booking;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class BookingClient {

	private final RequestSpecification spec;

	public BookingClient(RequestSpecification spec) {
		this.spec = spec;
	}

	// ---------------------------------------------------------
	// GET /booking
	// ---------------------------------------------------------

	public Response listBookings() {
		return given(spec).when().get("/booking");
	}

	public Response listBookingsByName(String firstname, String lastname) {

		RequestSpecification request = given(spec);

		if (firstname != null && !firstname.isBlank()) {
			request.queryParam("firstname", firstname);
		}

		if (lastname != null && !lastname.isBlank()) {
			request.queryParam("lastname", lastname);
		}

		return request.when().get("/booking");
	}

	public Response listBookingsByDates(String checkin, String checkout) {

		RequestSpecification request = given(spec);

		if (checkin != null && !checkin.isBlank()) {
			request.queryParam("checkin", checkin);
		}

		if (checkout != null && !checkout.isBlank()) {
			request.queryParam("checkout", checkout);
		}

		return request.when().get("/booking");
	}

	// ---------------------------------------------------------
	// GET /booking/{id}
	// ---------------------------------------------------------

	public Response getBooking(int id) {
		return given(spec).pathParam("id", id).when().get("/booking/{id}");
	}

	// ---------------------------------------------------------
	// POST /booking
	// ---------------------------------------------------------

	public Response createBooking(Booking booking) {
		return given(spec).body(booking).when().post("/booking");
	}

	public Response createBookingJson(String json) {
		return given(spec).body(json).when().post("/booking");
	}

	public Response createBookingXml(String xml) {
		return given().baseUri(ConfigManager.baseUri()).contentType("text/xml").accept("application/xml").body(xml)
				.when().post("/booking");
	}

	public Response createBookingUrlEncoded(Booking booking) {
		return given().baseUri(ConfigManager.baseUri()).contentType("application/x-www-form-urlencoded")
				.accept("application/json").formParam("firstname", booking.getFirstname())
				.formParam("lastname", booking.getLastname()).formParam("totalprice", booking.getTotalprice())
				.formParam("depositpaid", booking.isDepositpaid())
				.formParam("bookingdates[checkin]", booking.getBookingdates().getCheckin())
				.formParam("bookingdates[checkout]", booking.getBookingdates().getCheckout())
				.formParam("additionalneeds", booking.getAdditionalneeds()).when().post("/booking");
	}

	// ---------------------------------------------------------
	// PUT /booking/{id}
	// ---------------------------------------------------------

	public Response putBooking(int id, String token, Booking booking) {

		return given(spec).header("Cookie", "token=" + token).pathParam("id", id).body(booking).when()
				.put("/booking/{id}");
	}

	public Response putBookingBasicAuth(int id, Booking booking) {

		return given(spec).auth().preemptive().basic(ConfigManager.username(), ConfigManager.password())
				.pathParam("id", id).body(booking).when().put("/booking/{id}");
	}

	public Response putBookingJson(int id, String token, String json) {

		return given(spec).header("Cookie", "token=" + token).pathParam("id", id).body(json).when()
				.put("/booking/{id}");
	}

	// ---------------------------------------------------------
	// PATCH /booking/{id}
	// ---------------------------------------------------------

	public Response patchBooking(int id, String token, String json) {

		return given(spec).header("Cookie", "token=" + token).pathParam("id", id).body(json).when()
				.patch("/booking/{id}");
	}

	public Response patchBookingBasicAuth(int id, String json) {

		return given(spec).auth().preemptive().basic(ConfigManager.username(), ConfigManager.password())
				.pathParam("id", id).body(json).when().patch("/booking/{id}");
	}

	// ---------------------------------------------------------
	// DELETE /booking/{id}
	// ---------------------------------------------------------

	public Response deleteBooking(int id, String token) {

		return given(spec).header("Cookie", "token=" + token).pathParam("id", id).when().delete("/booking/{id}");
	}

	public Response deleteBookingWithoutAuth(int id) {

		return given(spec).pathParam("id", id).when().delete("/booking/{id}");
	}

	// =========================================================
	// Additional PUT / PATCH variants
	// =========================================================

	public Response putBookingWithoutAuth(int id, Booking booking) {
		return given(spec).pathParam("id", id).body(booking).when().put("/booking/{id}");
	}

	public Response patchBookingWithoutAuth(int id, String json) {
		return given(spec).pathParam("id", id).body(json).when().patch("/booking/{id}");
	}

	public Response putBookingWithAccept(int id, String token, Booking booking, String accept) {

		return given(spec).header("Cookie", "token=" + token).accept(accept).pathParam("id", id).body(booking).when()
				.put("/booking/{id}");
	}

	public Response patchBookingWithAccept(int id, String token, String json, String accept) {

		return given(spec).header("Cookie", "token=" + token).accept(accept).pathParam("id", id).body(json).when()
				.patch("/booking/{id}");
	}

	// =========================================================
	// DELETE authentication variants
	// =========================================================

	public Response deleteBookingBasicAuth(int id) {
		return given(spec).auth().preemptive().basic(ConfigManager.username(), ConfigManager.password())
				.pathParam("id", id).when().delete("/booking/{id}");
	}

	public Response deleteBookingWithInvalidToken(int id, String token) {

		return given(spec).header("Cookie", "token=" + token).pathParam("id", id).when().delete("/booking/{id}");
	}

	// =========================================================
	// Raw/custom request support for content negotiation
	// =========================================================

	public Response createBookingWithContentType(String body, String contentType, String accept) {

		return given().baseUri(ConfigManager.baseUri()).contentType(contentType).accept(accept).body(body).when()
				.post("/booking");
	}

	public Response getBookingWithAccept(int id, String accept) {

		return given(spec).accept(accept).pathParam("id", id).when().get("/booking/{id}");
	}
}
