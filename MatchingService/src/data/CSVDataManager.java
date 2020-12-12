package data;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
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

    public synchronized Iterable<CSVRecord> getCSVRecords(CSVFile csvFile) throws IOException {

        Reader in = new FileReader(csvFile.fullPath);
        Iterable<CSVRecord> records;

        records = CSVFormat.DEFAULT
                .withHeader(csvFile.headers)
                .withFirstRecordAsHeader()
                .parse(in);

        return records;
    }

    public synchronized void putCSVRecord(CSVFile csvFile, String... recordValues) throws IOException {
        FileWriter out = new FileWriter(csvFile.fullPath);
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                .withHeader(csvFile.headers))) {
            printer.printRecord(recordValues);
        }
    }

    public synchronized void putAllCSVRecords(CSVFile csvFile, String key, List<List<String>> values) throws IOException {
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

    public synchronized void putAllCSVRecords(CSVFile csvFile, List<List<String>> values) throws IOException {
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

        PUBLIC_KEYS_DOCTOR("pb_doctor.csv",new String[]{"date","key"}, Constants.PUBLIC_KEY_DIR_NAME),
        CAPSULES("matching_service_capsules.csv", new String[]{"arriveTime", "exitTime", "token", "tokenSign", "hash", "informed", "critical"}, Constants.MATCHING_SERVICE_DIR_NAME);

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
