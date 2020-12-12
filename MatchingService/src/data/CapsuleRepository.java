package data;

import org.apache.commons.csv.CSVRecord;
import service.Capsule;
import service.CriticalTuple;
import service.Token;
import util.Constants;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CapsuleRepository {
    private static CapsuleRepository instance;

    private CapsuleRepository() {
    }

    public static synchronized CapsuleRepository getInstance() {
        if (instance == null) {
            instance = new CapsuleRepository();
        }
        return instance;
    }

    public boolean isTokenAlreadySpent(Token token) throws IOException {
        List<Token> tokens = new ArrayList<>();
        Iterable<CSVRecord> records = CSVDataManager.getInstance().getCSVRecords(CSVDataManager.CSVFile.CAPSULES);
        if (records != null) {
            for (CSVRecord record : records) {
                tokens.add(new Token(record.get("tokenSign"), record.get("token")));
            }
        }
        if (!tokens.isEmpty()) {
            for (Token t : tokens)
                if (token.getSign().equals(t.getSign())) {
                    return false;
                }
        }
        return true;
    }

    public void addCapsule(Capsule capsule) throws IOException {
        List<List<String>> capsules = new ArrayList<>();
        Iterable<CSVRecord> records = CSVDataManager.getInstance().getCSVRecords(CSVDataManager.CSVFile.CAPSULES);
        if (records != null) {
            for (CSVRecord record : records) {
                capsules.add(new ArrayList<>(record.toMap().values()));
            }
        }
        capsules.add(Arrays.asList(capsule.getStartTime().toString(), capsule.getStopTime().toString(), capsule.getToken().getData(), capsule.getToken().getSign(), capsule.getHash(), "NA", "NA"));
        CSVDataManager.getInstance().putAllCSVRecords(CSVDataManager.CSVFile.CAPSULES, capsules);
    }

    public void markCapsulesCritical(String tokenSign, String hash, LocalDateTime arriveTime, LocalDateTime exitTime) throws IOException {
        List<List<String>> capsules = new ArrayList<>();
        List<List<String>> tempList = new ArrayList<>();
        Iterable<CSVRecord> records = CSVDataManager.getInstance().getCSVRecords(CSVDataManager.CSVFile.CAPSULES);
        if (records != null) {
            for (CSVRecord record : records) {
                capsules.add(new ArrayList<>(record.toMap().values()));
                LocalDateTime capsuleArriveTime = LocalDateTime.parse(record.get("arriveTime"));
                LocalDateTime capsuleExitTime = LocalDateTime.parse(record.get("exitTime"));
                if (record.get("hash").equals(hash) && !((capsuleExitTime.isBefore(arriveTime) && capsuleArriveTime.isBefore(arriveTime)) || (capsuleExitTime.isAfter(exitTime) && capsuleArriveTime.isAfter(exitTime)))) {
                    tempList.add(new ArrayList<>(record.toMap().values()));
                }
            }
        }
        capsules.removeAll(tempList);
        for (List<String> values : tempList) {
            if (!values.get(5).equals("1")) {
                values.set(5, "0");
            }
            values.set(6, "1");
            if (values.get(3).equals(tokenSign)) {
                values.set(5, "1");
            }
        }
        capsules.addAll(tempList);
        CSVDataManager.getInstance().putAllCSVRecords(CSVDataManager.CSVFile.CAPSULES, capsules);
    }

    public List<CriticalTuple> getCriticalUninformedCapsules(LocalDate date) throws IOException {
        List<CriticalTuple> tempList = new ArrayList<>();
        Iterable<CSVRecord> records = CSVDataManager.getInstance().getCSVRecords(CSVDataManager.CSVFile.CAPSULES);
        if (records != null) {
            for (CSVRecord record : records) {
                if (record.get("critical").equals("1") && record.get("informed").equals("0") && LocalDateTime.parse(record.get("arriveTime")).toLocalDate().isEqual(date)) {
                    tempList.add(new CriticalTuple(LocalDateTime.parse(record.get("arriveTime")), LocalDateTime.parse(record.get("exitTime")), record.get("hash")));
                }
            }
        }
        return tempList;
    }

    public void markCapsulesInformed(String criticalTokenSign) throws IOException {
        List<List<String>> capsules = new ArrayList<>();
        List<List<String>> tempList = new ArrayList<>();
        Iterable<CSVRecord> records = CSVDataManager.getInstance().getCSVRecords(CSVDataManager.CSVFile.CAPSULES);
        if (records != null) {
            for (CSVRecord record : records) {
                capsules.add(new ArrayList<>(record.toMap().values()));
                if (record.get("tokenSign").equals(criticalTokenSign) && record.get("informed").equals("0")) {
                    tempList.add(new ArrayList<>(record.toMap().values()));
                }
            }
        }
        capsules.removeAll(tempList);
        for (List<String> values : tempList) {
            values.set(5,"1");
        }
        capsules.addAll(tempList);
        CSVDataManager.getInstance().putAllCSVRecords(CSVDataManager.CSVFile.CAPSULES, capsules);
    }

    public List<String> getUninformedTokens(LocalDate date) throws IOException {

        List<String> tempList = new ArrayList<>();
        Iterable<CSVRecord> records = CSVDataManager.getInstance().getCSVRecords(CSVDataManager.CSVFile.CAPSULES);
        if (records != null) {
            for (CSVRecord record : records) {
                if (record.get("critical").equals("1") && record.get("informed").equals("0") && LocalDateTime.parse(record.get("arriveTime")).toLocalDate().isEqual(date)) {
                    tempList.add(record.get("tokenSign"));
                }
            }
        }
        return tempList;
    }

    public void flushCapsules() throws IOException {
        LocalDateTime date = LocalDateTime.now();
        List<List<String>> capsules = new ArrayList<>();
        List<List<String>> tempCapsules = new ArrayList<>();

        Iterable<CSVRecord> records = CSVDataManager.getInstance().getCSVRecords(CSVDataManager.CSVFile.CAPSULES);
        if (records != null) {
            for (CSVRecord record : records) {
                capsules.add(new ArrayList<>(record.toMap().values()));
            }
        }

        for (List<String> strings : capsules) {
            if (LocalDateTime.parse(strings.get(0)).plusSeconds(Constants.CAPSULE_FLUSH_DELAY).isBefore(date)) {

                // only flush the tokens that are not critical
                if ((strings.get(5).equals("NA") && strings.get(6).equals("NA")) || strings.get(6).equals("0") || strings.get(5).equals("0")) {
                    tempCapsules.add(strings);
                }
            }
        }

        capsules.removeAll(tempCapsules);
        CSVDataManager.getInstance().putAllCSVRecords(CSVDataManager.CSVFile.CAPSULES, capsules);
    }
}
