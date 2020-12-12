package data;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import util.Constants;

import java.io.*;
import java.security.PublicKey;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class CSVDataManager {

    private static CSVDataManager instance;

    private CSVDataManager() {

        // initiate files if they don't exist
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

    public String getCSVToString(CSVFile csvFile) throws IOException {
        StringBuilder sb = new StringBuilder();
        Iterable<CSVRecord> records = getCSVRecords(csvFile);
        sb.append(csvFile.filename).append("\n");
        for (CSVRecord record : records) {
            for (String string : record.toMap().values()) {
                sb.append(string).append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
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

    public void writePublicKeyToCsv(CSVFile csvFile,PublicKey key) throws IOException {
        // encode key material to a key encoding standard
        // the key is encoded with a base 64 encoder to a set of characters in A-Za-Zo-9+/
        // if the output is not a multiple of 3, the output will be padded with additional '=' s
        String keyValue = Base64.getEncoder().encodeToString(key.getEncoded());
        FileWriter out = new FileWriter(csvFile.fullPath);
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                .withHeader(csvFile.headers))) {
            printer.printRecord(LocalDate.now().toString(),keyValue);
        }
    }

    public enum CSVFile {

        PUBLIC_KEYS_MP("pb_mixing_proxy.csv",new String[]{"date","key"}, Constants.PUBLIC_KEY_DIR_NAME),
        PUBLIC_KEYS_REGISTRAR("pb_registrar.csv", new String[]{"date", "key"}, Constants.PUBLIC_KEY_DIR_NAME),
        CAPSULES("mixing_proxy_capsules.csv", new String[]{"startTime", "stopTime", "token", "tokenSign", "hash"},Constants.MIXING_PROXY_DIR_NAME),
        UNINFORMED_TOKENS("uninformed_tokens.csv",new String[]{"tokenSign"},Constants.MIXING_PROXY_DIR_NAME);

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
