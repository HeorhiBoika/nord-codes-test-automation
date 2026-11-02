package tests;

import constants.TestTags;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import model.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static constants.ProjectConstants.DO_ACTION_PATH;
import static constants.ResponseMessageConstants.TOKEN_NOT_FOUND;

@Feature("Выход из системы (LOGOUT)")
public class LogoutTest extends BaseApiTest {

    @Test
    @Tag(TestTags.SMOKE)
    @Tag(TestTags.REGRESSION)
    @DisplayName("После выхода из системы действия пользователю недоступны")
    @Description("Пользователь входит в систему и выходит из неё. Выход должен пройти успешно, "
            + "а следующее действие пользователя — быть отклонено без обращения к внешнему сервису.")
    public void logoutTest() {
        mockSteps.stubAuth(token, 200);
        ApiResponse loginResponse = appSteps.login(token);
        appSteps.checkSuccess(loginResponse);

        ApiResponse logoutResponse = appSteps.logout(token);
        appSteps.checkSuccess(logoutResponse);

        ApiResponse actionResponse = appSteps.action(token);
        appSteps.checkError(actionResponse, 403, String.format(TOKEN_NOT_FOUND, token));
        mockSteps.checkRequestNotSent(DO_ACTION_PATH, token);
    }
}
