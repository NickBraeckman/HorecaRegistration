package service;

import java.io.Serializable;

public class Acknowledgement implements Serializable {

    public String hash;
    public String sign;

    public Acknowledgement(String hash, String sign) {
        this.hash = hash;
        this.sign = sign;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
