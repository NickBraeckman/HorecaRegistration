package data;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import util.Config;

import java.io.*;
import java.util.Map;

public class CSVDataManager {

    private static CSVDataManager instance;

    private CSVDataManager() {
        initFiles();
    }

    public static synchronized CSVDataManager getInstance() {
        if (instance == null) {
            instance = new CSVDataManager();
        }
        return instance;
    }

    private void initFiles() {
        for (CSVFile csvFile : CSVFile.values()) {

            if (!csvFile.dirName.equals("")) {
                File directory = new File(csvFile.dirName);
                if (!directory.exists()) {
                    directory.mkdir();
                }
            }

            File file = new File(csvFile.fullPath);
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public Iterable<CSVRecord> getCSVRecords(CSVFile csvFile) throws IOException {
        Reader in = new FileReader(csvFile.fullPath);
        Iterable<CSVRecord> records;

        records = CSVFormat.DEFAULT
                .withHeader(csvFile.headers)
                .withFirstRecordAsHeader()
                .parse(in);

        return records;
    }

    public void putCSVRecord(CSVFile csvFile, String... recordValues) throws IOException {
        FileWriter out = new FileWriter(csvFile.fullPath);
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                .withHeader(csvFile.headers))) {
            printer.printRecord(recordValues);
        }
    }

    public void putAllCSVRecords(CSVFile csvFile, Map<String, String> records) throws IOException {
        FileWriter out = new FileWriter(csvFile.fullPath);
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                .withHeader(csvFile.headers))) {
            records.forEach((key, value) -> {
                try {
                    printer.printRecord(key, value);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public enum CSVFile {

        // monthly pseudonyms -> overwrite current database (monthly)
        PSEUDONYMS("catering_facility_pseudonyms.csv", new String[]{"day", "pseudonym"}, Config.DIR_NAME);

        private final String[] headers;
        private final String filename;
        private final String dirName;
        private final String fullPath;

        CSVFile(String filename, String[] headers, String dirName) {
            this.filename = filename;
            this.headers = headers;
            this.dirName = dirName;
            this.fullPath = dirName + "\\" + filename;
        }
    }
}
