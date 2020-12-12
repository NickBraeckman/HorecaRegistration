package service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Capsule implements Serializable {

    private String hash;
    private Token token;
    private LocalDateTime startTime;
    private LocalDateTime stopTime;
    public Capsule() {
    }

    public Capsule(String hash, Token token, LocalDateTime startTime, LocalDateTime stopTime) {
        this.hash = hash;
        this.token = token;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStopTime() {
        return stopTime;
    }

    public void setStopTime(LocalDateTime stopTime) {
        this.stopTime = stopTime;
    }

}
