package com.example.api.config;

public final class ConfigManager {
    private ConfigManager() {}

    public static String baseUri() {
        return System.getProperty("baseUrl", "https://restful-booker.herokuapp.com");
    }

    public static String username() {
        return System.getProperty("api.username", "admin");
    }

    public static String password() {
        return System.getProperty("api.password", "password123");
    }
}
