package data;

import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UninformedTokensRepository {

    private static UninformedTokensRepository instance;

    private UninformedTokensRepository() {
    }

    public static synchronized UninformedTokensRepository getInstance() {
        if (instance == null) {
            instance = new UninformedTokensRepository();
        }
        return instance;
    }

    public void addUninformedTokenSigns(List<String> uninformedTokenSigns) throws IOException {

        Iterable<CSVRecord> records = CSVDataManager.getInstance().getCSVRecords(CSVDataManager.CSVFile.UNINFORMED_TOKENS);
        List<List<String>> tokens = new ArrayList<>();

        for (CSVRecord record : records) {
            tokens.add(new ArrayList<>(record.toMap().values()));
        }

        for (String token : uninformedTokenSigns) {
            tokens.add(Collections.singletonList(token));
        }
        CSVDataManager.getInstance().putAllCSVRecords(CSVDataManager.CSVFile.UNINFORMED_TOKENS, tokens);
    }

    public List<String> flushUninformedTokens() throws IOException {
        Iterable<CSVRecord> records = CSVDataManager.getInstance().getCSVRecords(CSVDataManager.CSVFile.UNINFORMED_TOKENS);
        List<List<String>> tokens = new ArrayList<>();
        List<String> tokenSigns = new ArrayList<>();
        for (CSVRecord record : records) {
            tokens.add(new ArrayList<>(record.toMap().values()));
        }
        for (List<String> tokenTuple : tokens){
            tokenSigns.add(tokenTuple.get(0));
        }

        CSVDataManager.getInstance().putAllCSVRecords(CSVDataManager.CSVFile.UNINFORMED_TOKENS,new ArrayList<>());
        return tokenSigns;
    }

}
