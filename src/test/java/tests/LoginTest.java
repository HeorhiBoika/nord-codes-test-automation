package tests;

import constants.TestTags;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import model.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static constants.ProjectConstants.AUTH_PATH;
import static constants.ProjectConstants.DO_ACTION_PATH;
import static constants.ResponseMessageConstants.INTERNAL_SERVER_ERROR;
import static constants.ResponseMessageConstants.TOKEN_NOT_FOUND;

@Feature("Вход в систему (LOGIN)")
public class LoginTest extends BaseApiTest {

    @Test
    @Tag(TestTags.SMOKE)
    @Tag(TestTags.REGRESSION)
    @DisplayName("Успешный вход в систему")
    @Description("Пользователь входит в систему, внешний сервис подтверждает авторизацию. "
            + "Приложение должно ответить успехом и передать токен пользователя во внешний сервис.")
    public void loginTest() {
        mockSteps.stubAuth(token, 200);

        ApiResponse response = appSteps.login(token);

        appSteps.checkSuccess(response);
        mockSteps.checkRequestSent(AUTH_PATH, token);
    }

    @Test
    @Tag(TestTags.REGRESSION)
    @DisplayName("Вход невозможен, если внешний сервис вернул ошибку")
    @Description("Внешний сервис отвечает ошибкой на запрос авторизации. "
            + "Приложение должно отказать во входе и не запоминать пользователя: "
            + "следующее действие пользователя должно быть отклонено.")
    public void loginWithExternalServiceErrorTest() {
        mockSteps.stubAuth(token, 500);

        ApiResponse loginResponse = appSteps.login(token);
        appSteps.checkError(loginResponse, 500, INTERNAL_SERVER_ERROR);

        ApiResponse actionResponse = appSteps.action(token);
        appSteps.checkError(actionResponse, 403, String.format(TOKEN_NOT_FOUND, token));
        mockSteps.checkRequestNotSent(DO_ACTION_PATH, token);
    }
}
