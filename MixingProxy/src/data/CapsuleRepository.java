package data;

import org.apache.commons.csv.CSVRecord;
import service.Capsule;
import service.Token;
import util.Constants;

import java.io.IOException;
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
        List<String> tempCapsule = new ArrayList<>();
        Iterable<CSVRecord> records = CSVDataManager.getInstance().getCSVRecords(CSVDataManager.CSVFile.CAPSULES);
        if (records != null) {
            for (CSVRecord record : records) {
                capsules.add(new ArrayList<>(record.toMap().values()));
            }
        }
        capsules.add(Arrays.asList(capsule.getStartTime().toString(), capsule.getStopTime().toString(), capsule.getToken().getData(), capsule.getToken().getSign(), capsule.getHash()));
        CSVDataManager.getInstance().putAllCSVRecords(CSVDataManager.CSVFile.CAPSULES, capsules);
    }

    public boolean updateCapsule(Capsule capsule) throws IOException {
        List<List<String>> capsules = new ArrayList<>();
        List<String> tempCapsule = new ArrayList<>();
        Iterable<CSVRecord> records = CSVDataManager.getInstance().getCSVRecords(CSVDataManager.CSVFile.CAPSULES);

        if (records != null) {
            for (CSVRecord record : records) {
                if (capsule.getToken().getSign().equals(record.get("tokenSign")) && capsule.getHash().equals(record.get("hash"))) {
                    tempCapsule = new ArrayList<>(record.toMap().values());
                }
                capsules.add(new ArrayList<>(record.toMap().values()));
            }
        }
        // update capsule on visitor exit
        if (!tempCapsule.isEmpty()) {
            LocalDateTime arriveTime = LocalDateTime.parse(tempCapsule.get(0));
            LocalDateTime exitTime = LocalDateTime.parse(tempCapsule.get(1));

            // check the time frame
            if (arriveTime.isEqual(capsule.getStartTime()) && exitTime.isAfter(capsule.getStopTime())) {
                capsules.add(Arrays.asList(capsule.getStartTime().toString(), capsule.getStopTime().toString(), tempCapsule.get(2), tempCapsule.get(3), tempCapsule.get(4)));
                capsules.remove(tempCapsule);
                CSVDataManager.getInstance().putAllCSVRecords(CSVDataManager.CSVFile.CAPSULES, capsules);
                return true;
            }
        }
        return false;
    }

    public List<Capsule> flushCapsules() throws IOException {
        LocalDateTime date = LocalDateTime.now();
        List<List<String>> capsules = new ArrayList<>();
        List<List<String>> tempCapsules = new ArrayList<>();
        List<Capsule> flushedCapsules = new ArrayList<>();

        Iterable<CSVRecord> records = CSVDataManager.getInstance().getCSVRecords(CSVDataManager.CSVFile.CAPSULES);
        if (records != null) {
            for (CSVRecord record : records) {
                capsules.add(new ArrayList<>(record.toMap().values()));
            }
        }
        if (!capsules.isEmpty()) {
            for (List<String> strings : capsules) {
                // uncomment these in production -> demo purposes: flush button
                // if (LocalDateTime.parse(strings.get(1)).plusSeconds(Constants.CAPSULE_FLUSH_DELAY).isBefore(date)) {
                    tempCapsules.add(strings);
                    Capsule capsule = new Capsule();
                    capsule.setStartTime(LocalDateTime.parse(strings.get(0)));
                    capsule.setStopTime(LocalDateTime.parse(strings.get(1)));
                    Token token = new Token();
                    token.setData(strings.get(2));
                    token.setSign(strings.get(3));
                    capsule.setHash(strings.get(4));
                    capsule.setToken(token);
                    flushedCapsules.add(capsule);
               // }
            }
            capsules.removeAll(tempCapsules);
            CSVDataManager.getInstance().putAllCSVRecords(CSVDataManager.CSVFile.CAPSULES, capsules);
        }
        return flushedCapsules;
    }
}
