package data;


import org.apache.commons.csv.CSVRecord;
import service.CriticalTuple;
import util.Constants;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VisitorLogRepository {

    private final CSVDataManager dataManager;

    public VisitorLogRepository(CSVDataManager dataManager){
        this.dataManager = dataManager;
    }

    public void logQrCode(LocalDateTime arriveTime, QRCode qrCode, String tokenSign) throws IOException {
        List<List<String>> logs = new ArrayList<>();
        Iterable<CSVRecord> records = dataManager.getCSVRecords(CSVDataManager.CSVFile.VISITOR_LOG);
        if (records != null) {
            for (CSVRecord record : records) {
                logs.add(new ArrayList<>(record.toMap().values()));
            }
        }
        logs.add(Arrays.asList(arriveTime.toString(), arriveTime.plusSeconds(Constants.CAPSULE_VISITOR_FLUSH_DELAY).toString(), qrCode.getRi(), qrCode.getCf(), qrCode.getHash(), tokenSign));
        dataManager.putAllCSVRecords(CSVDataManager.CSVFile.VISITOR_LOG, logs);
    }

    public void logQrCodeExitTime(LocalDateTime arriveTime, LocalDateTime exitTime, QRCode qrCode, String tokenSign) throws IOException {
        List<List<String>> logs = new ArrayList<>();
        Iterable<CSVRecord> records = dataManager.getCSVRecords(CSVDataManager.CSVFile.VISITOR_LOG);
        List<String> log = new ArrayList<>();
        if (records != null) {
            for (CSVRecord record : records) {
                if (record.get("tokenSign").equals(tokenSign)) {
                    log = new ArrayList<>(Arrays.asList(arriveTime.toString(), exitTime.toString(), qrCode.ri, qrCode.cf, qrCode.hash, tokenSign));
                } else {
                    logs.add(new ArrayList<>(record.toMap().values()));
                }
            }
        }
        logs.add(log);
        dataManager.putAllCSVRecords(CSVDataManager.CSVFile.VISITOR_LOG, logs);
    }

    public List<String> getTokenSigns(String hash, LocalDateTime arriveTime, LocalDateTime exitTime) throws IOException {
        List<String> criticalTokenSigns = new ArrayList<>();
        Iterable<CSVRecord> records = dataManager.getCSVRecords(CSVDataManager.CSVFile.VISITOR_LOG);
            if (records != null) {
                for (CSVRecord record : records) {
                    LocalDateTime arriveTimeRecord = LocalDateTime.parse(record.get("arriveTime"));
                    LocalDateTime exitTimeRecord = LocalDateTime.parse(record.get("exitTime"));
                    if (record.get("hash").equals(hash) && !((arriveTimeRecord.isBefore(arriveTimeRecord) && exitTimeRecord.isBefore(arriveTime)) || (arriveTimeRecord.isAfter(exitTime) && exitTimeRecord.isAfter(exitTime)))) {
                        criticalTokenSigns.add(record.get("tokenSign"));
                    }
                }
            }

        return criticalTokenSigns;
    }

}
