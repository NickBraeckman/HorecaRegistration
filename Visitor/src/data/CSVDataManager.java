package data;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import util.Config;
import util.Constants;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVDataManager {

    public CSVDataManager() {
    }

    public void initFiles() {
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

    public void putAllCSVRecords(CSVFile csvFile, String key, List<List<String>> values) throws IOException {
        FileWriter out = new FileWriter(csvFile.fullPath);
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                .withHeader(csvFile.headers))) {
            for (List<String> value : values) {
                List<String> temp = new ArrayList<>();
                temp.add(key);
                temp.addAll(value);
                printer.printRecord(temp);
            }
        }

    }

    public void putAllCSVRecords(CSVFile csvFile, List<List<String>> values) throws IOException {
        FileWriter out = new FileWriter(csvFile.fullPath);
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                .withHeader(csvFile.headers))) {
            values.forEach((value) -> {
                try {
                    printer.printRecord(value);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }


    public enum CSVFile {

        PUBLIC_KEYS_MP("pb_mixing_proxy.csv", new String[]{"date", "key"}, Constants.PUBLIC_KEY_DIR_NAME),
        PUBLIC_KEYS_REGISTRAR("pb_registrar.csv", new String[]{"date", "key"}, Constants.PUBLIC_KEY_DIR_NAME),
        TOKENS("visitor_tokens.csv", new String[]{"date", "sign", "data"}, Config.DIR_NAME),
        VISITOR_LOG("visitor_log.csv", new String[]{"arriveTime", "exitTime", "ri", "cf", "hash", "tokenSign"}, Config.DIR_NAME);

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
