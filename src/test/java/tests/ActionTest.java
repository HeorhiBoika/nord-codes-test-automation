package tests;

import constants.TestTags;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import model.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static constants.ProjectConstants.DO_ACTION_PATH;
import static constants.ResponseMessageConstants.INTERNAL_SERVER_ERROR;
import static constants.ResponseMessageConstants.TOKEN_NOT_FOUND;

@Feature("Действие пользователя (ACTION)")
public class ActionTest extends BaseApiTest {

    @Test
    @Tag(TestTags.SMOKE)
    @Tag(TestTags.REGRESSION)
    @DisplayName("Вошедший пользователь успешно выполняет действие")
    @Description("Пользователь входит в систему и выполняет действие. "
            + "Приложение должно ответить успехом и передать действие во внешний сервис.")
    public void actionTest() {
        mockSteps.stubAuth(token, 200);
        mockSteps.stubDoAction(token, 200);
        ApiResponse loginResponse = appSteps.login(token);
        appSteps.checkSuccess(loginResponse);

        ApiResponse actionResponse = appSteps.action(token);

        appSteps.checkSuccess(actionResponse);
        mockSteps.checkRequestSent(DO_ACTION_PATH, token);
    }

    @Test
    @Tag(TestTags.REGRESSION)
    @DisplayName("Действие запрещено пользователю, который не входил в систему")
    @Description("Пользователь пытается выполнить действие без входа в систему. "
            + "Приложение должно отказать и не обращаться к внешнему сервису.")
    public void actionWithoutLoginTest() {
        ApiResponse response = appSteps.action(token);

        appSteps.checkError(response, 403, String.format(TOKEN_NOT_FOUND, token));
        mockSteps.checkNoRequests(token);
    }

    @Test
    @Tag(TestTags.REGRESSION)
    @DisplayName("Действие не выполнено, если внешний сервис вернул ошибку")
    @Description("Вошедший пользователь выполняет действие, но внешний сервис отвечает ошибкой. "
            + "Приложение должно сообщить пользователю об ошибке.")
    public void actionWithExternalServiceErrorTest() {
        mockSteps.stubAuth(token, 200);
        mockSteps.stubDoAction(token, 500);
        ApiResponse loginResponse = appSteps.login(token);
        appSteps.checkSuccess(loginResponse);

        ApiResponse actionResponse = appSteps.action(token);

        appSteps.checkError(actionResponse, 500, INTERNAL_SERVER_ERROR);
        mockSteps.checkRequestSent(DO_ACTION_PATH, token);
    }
}
