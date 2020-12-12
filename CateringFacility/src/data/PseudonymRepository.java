package data;

import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

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

    public String getDailyPseudonym() throws IOException {
        LocalDate localDate = LocalDate.now();
        Iterable<CSVRecord> records = CSVDataManager.getInstance().getCSVRecords(CSVDataManager.CSVFile.PSEUDONYMS);
        for (CSVRecord record : records) {
            LocalDate date = LocalDate.parse(record.get("day"));
            if (date.isEqual(localDate)) {
                return record.get("pseudonym");
            }
        }
        return null;
    }

    public void putAllPseudonyms(Map<String, String> pseudonymMapByDate) throws IOException {
        pseudonymMapByDate.forEach((day, pseudonym) -> {
            try {
                CSVDataManager.getInstance().putAllCSVRecords(CSVDataManager.CSVFile.PSEUDONYMS, pseudonymMapByDate);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
