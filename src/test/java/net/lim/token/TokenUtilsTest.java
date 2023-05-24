package net.lim.token;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class TokenUtilsTest {

    @Test
    public void testTokenCorrect() {
        String testUsername = "testUser";

        Token token = TokenUtils.issueToken(testUsername);

        Assertions.assertTrue(TokenUtils.verifyToken(testUsername, token.getTokenValue()));
    }

    @Test
    public void testTokenOwner() {
        String testUsername = "testUser";

        Token token = TokenUtils.issueToken(testUsername);

        Assertions.assertEquals(testUsername, token.getTokenOwner());
    }

    @Test
    public void testTokenInvalid() {
        String testUsername = "testUser";

        Token token = TokenUtils.issueToken(testUsername);

        Assertions.assertFalse(TokenUtils.verifyToken(testUsername + "2", token.getTokenValue()));
    }

}