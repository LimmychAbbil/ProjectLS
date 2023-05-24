package net.lim.token;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class TokenUtils {
    private static Map<String, Token> issuedTokens = new LinkedHashMap<>();

    private static Token generateToken(String userName) {
        return new Token(userName);
    }

    public static Token issueToken(String userName) {
        Token token = generateToken(userName);
        issuedTokens.put(userName, token);
        return token;
    }

    public static boolean verifyToken(String userName, byte[] tokenBytes) {
        return issuedTokens.containsKey(userName)
                && Arrays.equals(issuedTokens.get(userName).getTokenValue(), tokenBytes)
                && !issuedTokens.get(userName).isExpired();
    }
}
