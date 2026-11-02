package constants;

public final class ResponseMessageConstants {
    public static final String RESULT_OK = "OK";
    public static final String RESULT_ERROR = "ERROR";

    public static final String INVALID_API_KEY = "Missing or invalid API Key";
    public static final String INVALID_TOKEN = "token: должно соответствовать \"^[0-9A-F]{32}$\"";
    public static final String INVALID_ACTION = "action: invalid action '%s'. Allowed: LOGIN, LOGOUT, ACTION";
    public static final String TOKEN_NOT_FOUND = "Token '%s' not found";
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

    private ResponseMessageConstants() {}
}
