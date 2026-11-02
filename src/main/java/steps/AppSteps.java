package steps;

import api.EndpointApi;
import io.qameta.allure.Step;
import model.Action;
import model.ApiResponse;
import model.ResponseModel;
import model.TestContext;
import utils.RetrofitUtils;

import static constants.ProjectConstants.API_KEY;
import static constants.ResponseMessageConstants.RESULT_ERROR;
import static constants.ResponseMessageConstants.RESULT_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AppSteps {

    private final EndpointApi endpointApi;
    private final TestContext context;

    public AppSteps(EndpointApi endpointApi, TestContext context) {
        this.endpointApi = endpointApi;
        this.context = context;
    }

    @Step("Пользователь входит в систему (LOGIN) с токеном {token}")
    public ApiResponse login(String token) {
        return send(API_KEY, token, Action.LOGIN.name());
    }

    @Step("Пользователь выполняет действие (ACTION) с токеном {token}")
    public ApiResponse action(String token) {
        return send(API_KEY, token, Action.ACTION.name());
    }

    @Step("Пользователь выходит из системы (LOGOUT) с токеном {token}")
    public ApiResponse logout(String token) {
        return send(API_KEY, token, Action.LOGOUT.name());
    }

    @Step("Отправить запрос в приложение: action = {action}, token = {token}, X-Api-Key = {apiKey}")
    public ApiResponse send(String apiKey, String token, String action) {
        context.addUsedToken(token);
        ApiResponse response = RetrofitUtils.execute(endpointApi.endpoint(apiKey, token, action));
        updateLoggedInTokens(token, action, response);
        return response;
    }

    @Step("Проверить, что приложение ответило успешно: код 200, result = OK")
    public void checkSuccess(ApiResponse response) {
        ResponseModel body = response.getBody();
        assertNotNull(body, "Приложение вернуло пустой ответ");
        assertEquals(200, response.getStatusCode(), "Код ответа приложения");
        assertEquals(RESULT_OK, body.getResult(), "Поле result в ответе приложения");
        assertNull(body.getMessage(), "В успешном ответе не должно быть сообщения об ошибке");
    }

    @Step("Проверить, что приложение ответило ошибкой: код {statusCode}, сообщение «{message}»")
    public void checkError(ApiResponse response, int statusCode, String message) {
        ResponseModel body = response.getBody();
        assertNotNull(body, "Приложение вернуло пустой ответ");
        assertEquals(statusCode, response.getStatusCode(), "Код ответа приложения");
        assertEquals(RESULT_ERROR, body.getResult(), "Поле result в ответе приложения");
        assertEquals(message, body.getMessage(), "Сообщение об ошибке");
    }

    private void updateLoggedInTokens(String token, String action, ApiResponse response) {
        if (response.getBody() == null || !RESULT_OK.equals(response.getBody().getResult())) {
            return;
        }
        if (Action.LOGIN.name().equals(action)) {
            context.addLoggedInToken(token);
        }
        if (Action.LOGOUT.name().equals(action)) {
            context.removeLoggedInToken(token);
        }
    }
}
