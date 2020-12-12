package service;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CriticalTuple implements Serializable {
    public String hash;
    public LocalDateTime arriveTime;
    public LocalDateTime exitTime;

    public CriticalTuple(LocalDateTime arriveTime, LocalDateTime exitTime, String hash) {
        this.hash = hash;
        this.arriveTime = arriveTime;
        this.exitTime = exitTime;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public LocalDateTime getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(LocalDateTime arriveTime) {
        this.arriveTime = arriveTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }
}
