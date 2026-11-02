package steps;

import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import mock.WireMockService;
import model.TestContext;

import java.util.List;

import static constants.ProjectConstants.AUTH_PATH;
import static constants.ProjectConstants.DO_ACTION_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MockSteps {

    private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String JSON_CONTENT_TYPE = "application/json";

    private final WireMockService wireMockService;
    private final TestContext context;

    public MockSteps(WireMockService wireMockService, TestContext context) {
        this.wireMockService = wireMockService;
        this.context = context;
    }

    @Step("Внешний сервис отвечает кодом {statusCode} на запрос авторизации /auth")
    public void stubAuth(String token, int statusCode) {
        wireMockService.stub(context, AUTH_PATH, token, statusCode);
    }

    @Step("Внешний сервис отвечает кодом {statusCode} на запрос действия /doAction")
    public void stubDoAction(String token, int statusCode) {
        wireMockService.stub(context, DO_ACTION_PATH, token, statusCode);
    }

    @Step("Проверить, что приложение один раз отправило во внешний сервис корректный запрос {path}")
    public void checkRequestSent(String path, String token) {
        List<LoggedRequest> requests = wireMockService.findRequests(path, token);
        assertEquals(1, requests.size(), "Количество запросов " + path + " во внешний сервис");

        LoggedRequest request = requests.get(0);
        String contentType = request.getHeader("Content-Type");
        String accept = request.getHeader("Accept");

        assertEquals("token=" + token, request.getBodyAsString(), "Тело запроса " + path);
        assertNotNull(contentType, "Заголовок Content-Type запроса " + path);
        assertTrue(contentType.startsWith(FORM_CONTENT_TYPE), "Заголовок Content-Type запроса " + path + ": " + contentType);
        assertNotNull(accept, "Заголовок Accept запроса " + path);
        assertTrue(accept.contains(JSON_CONTENT_TYPE), "Заголовок Accept запроса " + path + ": " + accept);
    }

    @Step("Проверить, что приложение не отправляло запрос {path} во внешний сервис")
    public void checkRequestNotSent(String path, String token) {
        List<LoggedRequest> requests = wireMockService.findRequests(path, token);
        assertEquals(0, requests.size(), "Приложение не должно было обращаться к " + path);
    }

    @Step("Проверить, что приложение не обращалось к внешнему сервису")
    public void checkNoRequests(String token) {
        List<LoggedRequest> requests = wireMockService.findRequests(token);
        assertEquals(0, requests.size(), "Приложение не должно было обращаться к внешнему сервису");
    }

    public void attachReceivedRequests() {
        StringBuilder text = new StringBuilder();
        for (String token : context.getUsedTokens()) {
            for (LoggedRequest request : wireMockService.findRequests(token)) {
                text.append(request.getMethod()).append(" ").append(request.getUrl()).append("\n")
                        .append(request.getHeaders()).append("\n")
                        .append(request.getBodyAsString()).append("\n")
                        .append("----\n");
            }
        }
        if (text.isEmpty()) {
            text.append("Запросов не было");
        }
        Allure.addAttachment("Запросы, полученные внешним сервисом", "text/plain", text.toString(), ".txt");
    }
}
