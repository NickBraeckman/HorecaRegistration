package util;

import data.VisitorRepository;
import security.RegistrarSecurityManager;
import service.Token;
import service.TokenBatch;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TokenGenerator {

    public static TokenBatch generateInitTokens(String phoneNumber) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        return generateTokens(phoneNumber);
    }

    public static TokenBatch generateDailyTokens(String phoneNumber) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        if (VisitorRepository.getInstance().checkLastIssuedTokenBatch(phoneNumber)) {
            return generateTokens(phoneNumber);
        }
        return null;
    }

    private static TokenBatch generateTokens(String phoneNumber) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        List<Token> tokens = new ArrayList<>();
        for (int i = 0; i < Constants.DAILY_AMOUNT_OF_TOKENS; i++) {
            tokens.add(RegistrarSecurityManager.getInstance().generateToken(LocalDate.now()));
        }
        TokenBatch tokenBatch = new TokenBatch(tokens, LocalDateTime.now());
        VisitorRepository.getInstance().addTokenBatch(phoneNumber, tokenBatch);
        return tokenBatch;
    }

}
