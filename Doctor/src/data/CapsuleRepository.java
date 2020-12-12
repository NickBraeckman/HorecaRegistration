package data;

import org.apache.commons.csv.CSVRecord;
import service.InfectedCapsule;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CapsuleRepository {

    private static CapsuleRepository instance;

    private CapsuleRepository() {
    }

    public synchronized static CapsuleRepository getInstance() {
        if (instance == null) {
            instance = new CapsuleRepository();
        }
        return instance;
    }

    public void addInfectedCapsules(List<InfectedCapsule> infectedCapsules) throws IOException {
        Iterable<CSVRecord> records = CSVDataManager.getInstance().getCSVRecords(CSVDataManager.CSVFile.INFECTED_CAPSULES);
        List<List<String>> logs = new ArrayList<>();

        for (CSVRecord record : records) {
            logs.add(new ArrayList<>(record.toMap().values()));
        }
        for (InfectedCapsule capsule : infectedCapsules) {
            logs.add(Arrays.asList(capsule.getArriveTime().toString(), capsule.getExitTime().toString(), capsule.getHash(), capsule.getRi(), capsule.getDoctorSign(), capsule.getData(),capsule.getTokenSign()));
        }
        CSVDataManager.getInstance().putAllCSVRecords(CSVDataManager.CSVFile.INFECTED_CAPSULES,logs);
    }

    public List<InfectedCapsule> getInfectedCapsules() throws IOException {
        Iterable<CSVRecord> records = CSVDataManager.getInstance().getCSVRecords(CSVDataManager.CSVFile.INFECTED_CAPSULES);
        List<InfectedCapsule> infectedCapsules = new ArrayList<>();

        for (CSVRecord record : records) {
            InfectedCapsule infectedCapsule = new InfectedCapsule();
            infectedCapsule.setArriveTime(LocalDateTime.parse(record.get("arriveTime")));
            infectedCapsule.setExitTime(LocalDateTime.parse(record.get("exitTime")));
            infectedCapsule.setTokenSign(record.get("tokenSign"));
            infectedCapsule.setHash(record.get("hash"));
            infectedCapsule.setRi(record.get("ri"));
            infectedCapsule.setDoctorSign("doctorSign");
            infectedCapsule.setData("data");
            infectedCapsules.add(new InfectedCapsule());
        }
        return infectedCapsules;
    }

    // select log records in between a certain period of incubation
    public List<InfectedCapsule> getVisitorLog(LocalDateTime from, LocalDateTime until) throws IOException {
        Iterable<CSVRecord> records = CSVDataManager.getInstance().getCSVRecords(CSVDataManager.CSVFile.VISITOR_LOG);
        List<InfectedCapsule> infectedCapsules = new ArrayList<>();

        for (CSVRecord record : records) {
            LocalDateTime capsuleArriveTime = LocalDateTime.parse(record.get("arriveTime"));
            LocalDateTime capsuleExitTime = LocalDateTime.parse(record.get("exitTime"));
            if ((capsuleArriveTime.isAfter(from) && capsuleArriveTime.isBefore(until)) || (capsuleExitTime.isBefore(until) && capsuleExitTime.isAfter(from))) {
                InfectedCapsule capsule = new InfectedCapsule();
                capsule.setArriveTime(LocalDateTime.parse(record.get("arriveTime")));
                capsule.setExitTime(LocalDateTime.parse(record.get("exitTime")));
                capsule.setRi(record.get("ri"));
                capsule.setTokenSign(record.get("tokenSign"));
                capsule.setHash(record.get("hash"));
                infectedCapsules.add(capsule);
            }
        }
        return infectedCapsules;
    }

}
