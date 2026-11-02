package mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import lombok.extern.slf4j.Slf4j;
import model.TestContext;

import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static constants.ProjectConstants.MOCK_PORT;

@Slf4j
public class WireMockService {

    private static WireMockService instance;
    private final WireMockServer server;

    private WireMockService() {
        server = new WireMockServer(options().port(MOCK_PORT).extensions(new MockRequestLogger()));
        server.start();
        log.info("WireMock started on port {}", MOCK_PORT);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    }

    public static synchronized WireMockService getInstance() {
        if (instance == null) {
            instance = new WireMockService();
        }
        return instance;
    }

    public void stub(TestContext context, String path, String token, int statusCode) {
        StubMapping stub = server.stubFor(post(urlPathEqualTo(path))
                .withRequestBody(containing("token=" + token))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{}")));
        context.addStubId(stub.getId());
    }

    public List<LoggedRequest> findRequests(String path, String token) {
        return server.findAll(postRequestedFor(urlPathEqualTo(path)).withRequestBody(containing(token)));
    }

    public List<LoggedRequest> findRequests(String token) {
        return server.findAll(byToken(token));
    }

    public void cleanUp(TestContext context) {
        for (UUID stubId : context.getStubIds()) {
            server.removeStubMapping(stubId);
        }
        for (String token : context.getUsedTokens()) {
            server.removeServeEventsMatching(byToken(token).build());
        }
        log.info("MOCK cleanup: removed {} stub(s) and requests for tokens {}",
                context.getStubIds().size(), context.getUsedTokens());
    }

    private static RequestPatternBuilder byToken(String token) {
        return anyRequestedFor(anyUrl()).withRequestBody(containing(token));
    }
}
