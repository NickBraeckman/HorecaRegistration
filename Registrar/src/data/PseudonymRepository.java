package data;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PseudonymRepository {

    private static PseudonymRepository instance;


    private PseudonymRepository() {

    }

    public static synchronized PseudonymRepository getInstance() {
        if (instance == null) {
            instance = new PseudonymRepository();
        }
        return instance;
    }

    public void putPseudonym(String date, String pseudonym) throws IOException {
        Iterable<CSVRecord> records = CSVDataManager.getInstance().getCSVRecords(CSVDataManager.CSVFile.PSEUDONYMS);
        List<List<String>> tempPseudonyms = new ArrayList<>();
        for (CSVRecord record : records) {
            tempPseudonyms.add(Arrays.asList(new String[]{record.get("date"), record.get("pseudonym")}));
        }
        tempPseudonyms.add(Arrays.asList(new String[]{date, pseudonym}));
        CSVDataManager.getInstance().putAllCSVRecords(CSVDataManager.CSVFile.PSEUDONYMS,tempPseudonyms);
    }

    public List<String> getPseudonyms(LocalDate date) throws IOException {
        Iterable<CSVRecord> records = CSVDataManager.getInstance().getCSVRecords(CSVDataManager.CSVFile.PSEUDONYMS);
        List<java.lang.String> tempPseudonyms = new ArrayList<>();
        for (CSVRecord record : records) {
            if (LocalDate.parse(record.get("date")).isEqual(date)) {
                tempPseudonyms.add(record.get("pseudonym"));
            }
        }
        return tempPseudonyms;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        return super.toString();
    }
}
