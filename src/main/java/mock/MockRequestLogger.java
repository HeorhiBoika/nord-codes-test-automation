package mock;

import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ServeEventListener;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockRequestLogger implements ServeEventListener {

    @Override
    public String getName() {
        return "mock-request-logger";
    }

    @Override
    public void afterComplete(ServeEvent serveEvent, Parameters parameters) {
        LoggedRequest request = serveEvent.getRequest();
        int status = serveEvent.getResponse().getStatus();

        if (serveEvent.getWasMatched()) {
            log.info("MOCK <-- {} {} body=[{}] -> {}", request.getMethod(), request.getUrl(), request.getBodyAsString(), status);
        } else {
            log.warn("MOCK <-- {} {} body=[{}] -> {} (no stub matched)", request.getMethod(), request.getUrl(), request.getBodyAsString(), status);
        }
    }
}
