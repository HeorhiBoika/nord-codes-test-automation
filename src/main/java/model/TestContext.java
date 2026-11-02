package model;

import lombok.Getter;
import utils.TestDataUtils;

import java.util.*;

@Getter
public class TestContext {

    private final String token = TestDataUtils.generateUniqueToken();
    private final List<UUID> stubIds = new ArrayList<>();
    private final Set<String> usedTokens = new LinkedHashSet<>();
    private final Set<String> loggedInTokens = new LinkedHashSet<>();

    public void addStubId(UUID stubId) {
        stubIds.add(stubId);
    }

    public void addUsedToken(String token) {
        if (token != null) {
            usedTokens.add(token);
        }
    }

    public void addLoggedInToken(String token) {
        loggedInTokens.add(token);
    }

    public void removeLoggedInToken(String token) {
        loggedInTokens.remove(token);
    }
}
