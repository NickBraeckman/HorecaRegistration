package data;

public class QRCode {
    public String ri;
    public String hash;
    public String cf;

    public QRCode(String ri, String cf, String hash) {
        this.ri = ri;
        this.hash = hash;
        this.cf = cf;
    }

    public String getRi() {
        return ri;
    }

    public void setRi(String ri) {
        this.ri = ri;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getCf() {
        return cf;
    }

    public void setCf(String cf) {
        this.cf = cf;
    }
}
