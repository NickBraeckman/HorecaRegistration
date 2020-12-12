package data;

import org.apache.commons.csv.CSVRecord;
import service.Token;
import service.TokenBatch;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TokenRepository {


    private final CSVDataManager dataManager;

    public TokenRepository(CSVDataManager dataManager) {
        this.dataManager = dataManager;
    }

    public void initFiles() {
        if (dataManager != null) {
            dataManager.initFiles();
        }
    }

    public Token getToken() throws IOException {

        LocalDateTime dateTime = null;
        Token token = null;
        List<Token> tokens = new ArrayList<>();
        Iterable<CSVRecord> records = dataManager.getCSVRecords(CSVDataManager.CSVFile.TOKENS);
        for (CSVRecord record : records) {
            if (record != null) {
                dateTime = LocalDateTime.parse(record.get("date"));
                tokens.add(new Token(record.get("sign"), record.get("data")));
            }
        }
        if (!tokens.isEmpty() && dateTime != null) {
            if (dateTime.plusDays(1).compareTo(LocalDateTime.now()) >= 0) {
                token = tokens.remove(0);
                putAllTokens(new TokenBatch(tokens, dateTime));
            } else return null;
        }

        return token;
    }

    public boolean checkLastIssued(LocalDateTime localDateTime) throws IOException {
        Iterable<CSVRecord> records = dataManager.getCSVRecords(CSVDataManager.CSVFile.TOKENS);
        for (CSVRecord record : records) {
            LocalDateTime temp = LocalDateTime.parse(record.get("date"));
            if (temp.plusDays(1).compareTo(localDateTime) >= 0) {
                return true;
            }
        }
        return false;
    }

    public void putAllTokens(TokenBatch tokenBatch) throws IOException {
        List<Token> tokenList = tokenBatch.getTokenList();
        LocalDateTime dateTime = tokenBatch.getLocalDateTime();
        List<List<String>> tokenValues = new ArrayList<>();
        tokenList.forEach(token -> {
            tokenValues.add(Arrays.asList(token.getSign(), token.getData()));
        });
        dataManager.putAllCSVRecords(CSVDataManager.CSVFile.TOKENS, dateTime.toString(), tokenValues);
    }

    public int getTokenCount() throws IOException {

        Iterable<CSVRecord> records = dataManager.getCSVRecords(CSVDataManager.CSVFile.TOKENS);
        int count = 0;
        for (CSVRecord record : records) {
            count++;
        }

        return count;
    }
}
