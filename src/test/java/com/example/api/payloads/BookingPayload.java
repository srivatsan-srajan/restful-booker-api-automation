package com.example.api.payloads;

public final class BookingPayload {
    private BookingPayload() {}

    public static String validBookingJson() {
        return """
                {
                  "firstname": "Srivatsan",
                  "lastname": "Automation",
                  "totalprice": 1250,
                  "depositpaid": true,
                  "bookingdates": {
                    "checkin": "2026-10-01",
                    "checkout": "2026-10-05"
                  },
                  "additionalneeds": "Breakfast"
                }
                """;
    }

    public static String updateBookingJson() {
        return """
                {
                  "firstname": "Srivatsan",
                  "lastname": "Updated",
                  "totalprice": 1500,
                  "depositpaid": true,
                  "bookingdates": {
                    "checkin": "2026-10-02",
                    "checkout": "2026-10-06"
                  },
                  "additionalneeds": "Breakfast and WiFi"
                }
                """;
    }

    public static String patchAdditionalNeedsJson() {
        return """
                {
                  "additionalneeds": "Airport pickup"
                }
                """;
    }

    public static String invalidBookingMissingFirstname() {
        return """
                {
                  "lastname": "Automation",
                  "totalprice": 100,
                  "depositpaid": true,
                  "bookingdates": {
                    "checkin": "2026-10-01",
                    "checkout": "2026-10-05"
                  }
                }
                """;
    }

    public static String xmlBooking() {
        return """
                <booking>
                  <firstname>Xml</firstname>
                  <lastname>Automation</lastname>
                  <totalprice>500</totalprice>
                  <depositpaid>true</depositpaid>
                  <bookingdates>
                    <checkin>2026-11-01</checkin>
                    <checkout>2026-11-05</checkout>
                  </bookingdates>
                  <additionalneeds>Breakfast</additionalneeds>
                </booking>
                """;
    }
}
