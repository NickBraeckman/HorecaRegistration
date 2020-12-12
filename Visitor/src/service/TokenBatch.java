package service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class TokenBatch implements Serializable {

    public List<Token> tokenList;
    public LocalDateTime localDateTime;

    public TokenBatch(List<Token> tokenList, LocalDateTime localDateTime) {
        this.tokenList = tokenList;
        this.localDateTime = localDateTime;
    }

    public List<Token> getTokenList() {
        return tokenList;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
    public void setTokenList(List<Token> tokenList) {
        this.tokenList = tokenList;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(localDateTime.toString()).append(":");
        tokenList.forEach(token -> builder.append("\n"+token.toString()).append(" "));
        return builder.toString();
    }
}
