package com.idw_signage.userauthentication.utils;

public class AlertConstants {

    private AlertConstants() {
        throw new AssertionError("Instantiating utility class...");
    }

    // Alert messages
    public static final String USER_NOT_FOUND = "User not found";
    public static final String INVALID_CREDENTIALS = "Invalid credentials";
    public static final String ACCOUNT_LOCKED = "Account is locked";
    public static final String PASSWORD_EXPIRED = "Password has expired"; public static final String INVALID_USERNAME_OR_PASSWORD = "Invalid username or password.";
    public static final String FAILED_TO_GENERATE_TOKEN = "Failed to generate token.";
    public static final String USERNAME_AND_PASSWORD_REQUIRED = "Username and password must be provided.";



    // Alert codes
    public static final int ERROR_USER_NOT_FOUND = 1001;
    public static final int ERROR_INVALID_CREDENTIALS = 1002;
    public static final int ERROR_ACCOUNT_LOCKED = 1003;
    public static final int ERROR_PASSWORD_EXPIRED = 1004;

    // Other constants
    public static final String ALERT_LEVEL_INFO = "INFO";
    public static final String ALERT_LEVEL_WARN = "WARN";
    public static final String ALERT_LEVEL_ERROR = "ERROR";
}
