package listeners;

import io.qameta.allure.listener.StepLifecycleListener;
import io.qameta.allure.listener.TestLifecycleListener;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.model.TestResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingListener implements TestLifecycleListener, StepLifecycleListener {

    @Override
    public void beforeTestStart(TestResult result) {
        log.info("========== START TEST: {} [{}]", result.getName(), result.getFullName());
    }

    @Override
    public void afterTestStop(TestResult result) {
        if (result.getStatus() == Status.PASSED) {
            log.info("========== PASSED: {}", result.getName());
        } else {
            log.error("========== {}: {} | {}", result.getStatus(), result.getName(), getMessage(result.getStatusDetails()));
        }
    }

    @Override
    public void beforeStepStart(StepResult result) {
        log.info("STEP: {}", result.getName());
    }

    @Override
    public void afterStepStop(StepResult result) {
        if (result.getStatus() != null && result.getStatus() != Status.PASSED) {
            log.error("STEP {}: {} | {}", result.getStatus(), result.getName(), getMessage(result.getStatusDetails()));
        }
    }

    private static String getMessage(StatusDetails details) {
        if (details == null || details.getMessage() == null) {
            return "";
        }
        return details.getMessage().replace("\n", " ");
    }
}
