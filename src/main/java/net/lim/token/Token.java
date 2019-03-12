package net.lim.token;

import java.security.SecureRandom;
import java.time.LocalDateTime;

public class Token {

    private String tokenOwner;

    private LocalDateTime expiredTime;

    private byte[] tokenValue;

    public Token(String tokenOwner) {
        this.tokenOwner = tokenOwner;
        this.expiredTime = LocalDateTime.now().plusMinutes(5);
        this.tokenValue = generateTokenValue();
    }

    public String getTokenOwner() {
        return tokenOwner;
    }

    public byte[] getTokenValue() {
        return tokenValue;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isBefore(expiredTime);
    }

    private byte[] generateTokenValue() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[15];
        random.nextBytes(bytes);

        return bytes;
    }
}
