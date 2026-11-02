package tests;

import api.EndpointApi;
import api.RetrofitClient;
import constants.ProjectConstants;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import mock.WireMockService;
import model.TestContext;
import org.junit.jupiter.api.AfterEach;
import steps.AppSteps;
import steps.MockSteps;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BaseApiTest {

    protected final EndpointApi endpointApi;
    protected final WireMockService wireMockService;
    protected final TestContext context;
    protected final AppSteps appSteps;
    protected final MockSteps mockSteps;
    protected final String token;

    public BaseApiTest() {
        this.endpointApi = RetrofitClient.getInstance().getRetrofitObject(EndpointApi.class, ProjectConstants.APP_BASE_URL);
        this.wireMockService = WireMockService.getInstance();
        this.context = new TestContext();
        this.appSteps = new AppSteps(endpointApi, context);
        this.mockSteps = new MockSteps(wireMockService, context);
        this.token = context.getToken();
    }

    @AfterEach
    public void tearDown() {
        mockSteps.attachReceivedRequests();
        cleanUpTestData();
    }

    @Step("Очистка тестовых данных")
    private void cleanUpTestData() {
        List<String> tokensToLogout = new ArrayList<>(context.getLoggedInTokens());
        for (String loggedInToken : tokensToLogout) {
            try {
                appSteps.logout(loggedInToken);
            } catch (RuntimeException e) {
                log.warn("Logout of token {} failed during clean up", loggedInToken, e);
            }
        }
        wireMockService.cleanUp(context);
    }
}
