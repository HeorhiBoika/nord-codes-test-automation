package constants;

public final class ProjectConstants {

    public static final String APP_BASE_URL = System.getProperty("app.baseUrl", "http://localhost:8080/");
    public static final String API_KEY = System.getProperty("app.apiKey", "qazWSXedc");
    public static final int MOCK_PORT = Integer.parseInt(System.getProperty("mock.port", "8888"));

    public static final String ENDPOINT_PATH = "/endpoint";
    public static final String AUTH_PATH = "/auth";
    public static final String DO_ACTION_PATH = "/doAction";

    public static final int TOKEN_LENGTH = 32;
    public static final String TOKEN_CHARS = "0123456789ABCDEF";
    public static final String LETTERS_G_TO_Z = "GHIJKLMNOPQRSTUVWXYZ";

    private ProjectConstants() {}
}
