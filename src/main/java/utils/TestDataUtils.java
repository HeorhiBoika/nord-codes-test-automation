package utils;

import java.security.SecureRandom;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static constants.ProjectConstants.LETTERS_G_TO_Z;
import static constants.ProjectConstants.TOKEN_CHARS;
import static constants.ProjectConstants.TOKEN_LENGTH;

public class TestDataUtils {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Set<String> ISSUED_TOKENS = ConcurrentHashMap.newKeySet();

    public static String generateUniqueToken() {
        return generateUniqueToken(TOKEN_LENGTH, TOKEN_CHARS);
    }

    public static String tokenOfLength(int length) {
        return generateUniqueToken(length, TOKEN_CHARS);
    }

    public static String lowercaseToken() {
        return generateUniqueToken(TOKEN_LENGTH, "abcdef");
    }

    public static String tokenWithLettersGToZ() {
        return generateUniqueToken(TOKEN_LENGTH, LETTERS_G_TO_Z);
    }

    private static String generateUniqueToken(int length, String chars) {
        String token;
        do {
            token = randomString(length, chars);
        } while (!ISSUED_TOKENS.add(token));
        return token;
    }

    private static String randomString(int length, String chars) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return builder.toString();
    }
}
