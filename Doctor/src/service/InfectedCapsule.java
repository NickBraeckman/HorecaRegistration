package service;

import java.io.Serializable;
import java.time.LocalDateTime;

public class InfectedCapsule implements Serializable {
    String tokenSign;
    String hash;
    String ri;
    LocalDateTime arriveTime;
    LocalDateTime exitTime;
    String doctorSign;
    String data;

    public InfectedCapsule(String tokenSign, String hash, String ri, LocalDateTime arriveTime, LocalDateTime exitTime, String doctorSign, String data) {
        this.tokenSign = tokenSign;
        this.hash = hash;
        this.ri = ri;
        this.arriveTime = arriveTime;
        this.exitTime = exitTime;
        this.doctorSign = doctorSign;
        this.data = data;
    }

    public InfectedCapsule() {
    }

    public String getTokenSign() {
        return tokenSign;
    }

    public void setTokenSign(String tokenSign) {
        this.tokenSign = tokenSign;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getRi() {
        return ri;
    }

    public void setRi(String ri) {
        this.ri = ri;
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

    public String getDoctorSign() {
        return doctorSign;
    }

    public void setDoctorSign(String doctorSign) {
        this.doctorSign = doctorSign;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
