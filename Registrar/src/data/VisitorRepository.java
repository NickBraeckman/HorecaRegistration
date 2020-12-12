package data;


import org.apache.commons.csv.CSVRecord;
import service.Token;
import service.TokenBatch;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class VisitorRepository {

    private static VisitorRepository instance;

    private Map<String, String> visitorMap;

    private VisitorRepository() {

        visitorMap = new HashMap<>();
    }

    public static synchronized VisitorRepository getInstance() {
        if (instance == null) {
            instance = new VisitorRepository();
        }
        return instance;
    }

    public boolean addVisitor(String phoneNumber, String password) {
        if (visitorMap.containsKey(phoneNumber)) {
            return false;
        }
        visitorMap.put(phoneNumber, password);
        return true;
    }

    public String getVisitorByTokenSign(String tokenSign) {
        Iterable<CSVRecord> records = null;
        try {
            records = CSVDataManager.getInstance().getCSVRecords(CSVDataManager.CSVFile.TOKENS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String phoneNumber = null;

        for (CSVRecord record : records) {
            if (record.get("tokenSign").equals(tokenSign)) {
                phoneNumber = record.get("phoneNumber");
            }
        }
        return phoneNumber;
    }

    public boolean isVisitorAuthenticated(String phoneNumber, String password) {
        if (!visitorMap.containsKey(phoneNumber)) {
            return false;
        }
        if (!visitorMap.get(phoneNumber).equals(password)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isVisitorRegistered(String phoneNumber) throws IOException {
        Iterable<CSVRecord> records = CSVDataManager.getInstance().getCSVRecords(CSVDataManager.CSVFile.VISITORS);
        List<List<String>> visitors = new ArrayList<>();
        for (CSVRecord record : records) {
            visitors.add(Arrays.asList(record.get("phoneNumber")));
            if (record.get("phoneNumber").equals(phoneNumber)) {
                return true;
            }
        }
        visitors.add(Arrays.asList(phoneNumber));
        CSVDataManager.getInstance().putAllCSVRecords(CSVDataManager.CSVFile.VISITORS, visitors);
        return false;
    }

    public void addTokenBatch(String phoneNumber, TokenBatch tokenBatch) throws IOException {
        Iterable<CSVRecord> records = CSVDataManager.getInstance().getCSVRecords(CSVDataManager.CSVFile.TOKENS);
        List<List<String>> tokens = new ArrayList<>();
        for (CSVRecord record : records) {
            tokens.add(new ArrayList<>(record.toMap().values()));
        }
        for (Token token : tokenBatch.tokenList) {
            List<String> values = new ArrayList<>();
            values.add(phoneNumber);
            values.add(tokenBatch.getLocalDateTime().toString());
            values.add(token.data);
            values.add(token.sign);
            tokens.add(values);
        }
        CSVDataManager.getInstance().putAllCSVRecords(CSVDataManager.CSVFile.TOKENS, tokens);
    }

    public boolean checkLastIssuedTokenBatch(String phoneNumber) throws IOException {
        Iterable<CSVRecord> records = CSVDataManager.getInstance().getCSVRecords(CSVDataManager.CSVFile.TOKENS);
        List<List<String>> tokens = new ArrayList<>();
        for (CSVRecord record : records) {
            tokens.add(new ArrayList<>(record.toMap().values()));
        }
        for (List<String> token : tokens) {
            if (token.get(0).equals(phoneNumber)) {
                if (LocalDateTime.parse(token.get(1)).plusDays(1).isBefore(LocalDateTime.now())) {
                    return true;
                }
            }
        }
        return false;
    }
}
