package tests;

import constants.TestTags;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import model.ApiResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import utils.TestDataUtils;

import static constants.ProjectConstants.API_KEY;
import static constants.ProjectConstants.AUTH_PATH;
import static constants.ResponseMessageConstants.INVALID_ACTION;
import static constants.ResponseMessageConstants.INVALID_API_KEY;
import static constants.ResponseMessageConstants.INVALID_TOKEN;

@Feature("Проверка входящих запросов")
public class RequestValidationTest extends BaseApiTest {

    private static final String LOGIN = "LOGIN";
    private static final String UNKNOWN_ACTION = "UNKNOWN";
    private static final String WRONG_API_KEY = "WRONG_API_KEY";

    @Test
    @Tag(TestTags.REGRESSION)
    @DisplayName("Запрос без API-ключа отклоняется")
    @Description("Запрос приходит без заголовка X-Api-Key. "
            + "Приложение должно отказать в доступе и не обращаться к внешнему сервису.")
    public void loginWithoutApiKeyTest() {
        ApiResponse response = appSteps.send(null, token, LOGIN);

        appSteps.checkError(response, 401, INVALID_API_KEY);
        mockSteps.checkNoRequests(token);
    }

    @Test
    @Tag(TestTags.REGRESSION)
    @DisplayName("Запрос с неверным API-ключом отклоняется")
    @Description("Запрос приходит с неверным значением заголовка X-Api-Key. "
            + "Приложение должно отказать в доступе и не обращаться к внешнему сервису.")
    public void loginWithWrongApiKeyTest() {
        ApiResponse response = appSteps.send(WRONG_API_KEY, token, LOGIN);

        appSteps.checkError(response, 401, INVALID_API_KEY);
        mockSteps.checkNoRequests(token);
    }

    @Test
    @Tag(TestTags.REGRESSION)
    @DisplayName("Вход с токеном из 31 символа отклоняется")
    @Description("Токен должен состоять ровно из 32 символов. Проверяется нижняя граница: 31 символ. "
            + "Приложение должно отклонить запрос и не обращаться к внешнему сервису.")
    public void loginWithShortTokenTest() {
        String shortToken = TestDataUtils.tokenOfLength(31);

        ApiResponse response = appSteps.login(shortToken);

        appSteps.checkError(response, 400, INVALID_TOKEN);
        mockSteps.checkNoRequests(shortToken);
    }

    @Test
    @Tag(TestTags.REGRESSION)
    @DisplayName("Вход с токеном из 33 символов отклоняется")
    @Description("Токен должен состоять ровно из 32 символов. Проверяется верхняя граница: 33 символа. "
            + "Приложение должно отклонить запрос и не обращаться к внешнему сервису.")
    public void loginWithLongTokenTest() {
        String longToken = TestDataUtils.tokenOfLength(33);

        ApiResponse response = appSteps.login(longToken);

        appSteps.checkError(response, 400, INVALID_TOKEN);
        mockSteps.checkNoRequests(longToken);
    }

    @Test
    @Tag(TestTags.REGRESSION)
    @DisplayName("Вход с токеном из строчных букв отклоняется")
    @Description("Токен может содержать только заглавные латинские буквы и цифры. "
            + "Приложение должно отклонить токен из строчных букв и не обращаться к внешнему сервису.")
    public void loginWithLowercaseTokenTest() {
        String lowercaseToken = TestDataUtils.lowercaseToken();

        ApiResponse response = appSteps.login(lowercaseToken);

        appSteps.checkError(response, 400, INVALID_TOKEN);
        mockSteps.checkNoRequests(lowercaseToken);
    }

    @Test
    @Tag(TestTags.REGRESSION)
    @Disabled("Дефект: приложение отклоняет токен с буквами G-Z (ответ 400), хотя по требованиям допустимы A-Z0-9. "
            + "Включить после исправления")
    @DisplayName("Вход с токеном из букв G-Z, допустимым по требованиям")
    @Description("По требованиям токен может содержать любые заглавные латинские буквы A-Z и цифры. "
            + "Пользователь с токеном, содержащим буквы G-Z, должен успешно войти в систему. "
            + "Дефект: приложение принимает только буквы A-F (шестнадцатеричный формат) и отклоняет такой токен.")
    public void loginWithLettersGToZTokenTest() {
        String tokenWithLettersGToZ = TestDataUtils.tokenWithLettersGToZ();
        mockSteps.stubAuth(tokenWithLettersGToZ, 200);

        ApiResponse response = appSteps.login(tokenWithLettersGToZ);

        appSteps.checkSuccess(response);
        mockSteps.checkRequestSent(AUTH_PATH, tokenWithLettersGToZ);
    }

    @Test
    @Tag(TestTags.REGRESSION)
    @DisplayName("Неизвестное действие отклоняется")
    @Description("Пользователь отправляет действие, которого нет в списке допустимых (LOGIN, ACTION, LOGOUT). "
            + "Приложение должно отклонить запрос и сообщить, какие действия допустимы.")
    public void unknownActionTest() {
        ApiResponse response = appSteps.send(API_KEY, token, UNKNOWN_ACTION);

        appSteps.checkError(response, 400, String.format(INVALID_ACTION, UNKNOWN_ACTION));
        mockSteps.checkNoRequests(token);
    }
}
